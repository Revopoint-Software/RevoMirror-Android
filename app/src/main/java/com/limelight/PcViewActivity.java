package com.limelight;

import android.app.ActivityManager;
import android.app.Service;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.res.Configuration;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.opengl.GLSurfaceView;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AbsListView;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.limelight.binding.PlatformBinding;
import com.limelight.binding.crypto.AndroidCryptoProvider;
import com.limelight.computers.ComputerManagerListener;
import com.limelight.computers.ComputerManagerService;
import com.limelight.grid.assets.DiskAssetLoader;
import com.limelight.nvstream.http.ComputerDetails;
import com.limelight.nvstream.http.NvApp;
import com.limelight.nvstream.http.NvHTTP;
import com.limelight.nvstream.http.PairingManager;
import com.limelight.nvstream.http.PairingManager.PairState;
import com.limelight.nvstream.wol.WakeOnLanSender;
import com.limelight.preferences.GlPreferences;
import com.limelight.ui.AdapterFragmentCallbacks;
import com.limelight.ui.activity.BaseActivity;
import com.limelight.ui.adapter.ComputerAdapter;
import com.limelight.ui.dialog.BottomMenuDialog;
import com.limelight.ui.dialog.PairPcDialog;
import com.limelight.utils.Dialog;
import com.limelight.utils.EventHub;
import com.limelight.utils.HelpLauncher;
import com.limelight.utils.LanguageUtils;
import com.limelight.utils.LimeLog;
import com.limelight.utils.LogUtil;
import com.limelight.utils.ScreenUtil;
import com.limelight.utils.ServerHelper;
import com.limelight.utils.SharedPreferenceUtil;
import com.limelight.utils.ShortcutHelper;
import com.limelight.utils.UiHelper;
import com.limelight.view.GridSpacingItemDecoration;

import org.xmlpull.v1.XmlPullParserException;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.UnknownHostException;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

public class PcViewActivity extends BaseActivity implements AdapterFragmentCallbacks {
    private RecyclerView recyclerView;
    private ComputerAdapter computerAdapter;
    private ShortcutHelper shortcutHelper;
    private ComputerManagerService.ComputerManagerBinder managerBinder;
    private boolean freezeUpdates, runningPolling, inForeground, completeOnCreateCalled;
    private PairPcDialog pairPcDialog;

    private RelativeLayout layoutNoConnect, layoutContent;

    private final ServiceConnection serviceConnection = new ServiceConnection() {
        public void onServiceConnected(ComponentName className, IBinder binder) {
            final ComputerManagerService.ComputerManagerBinder localBinder =
                    ((ComputerManagerService.ComputerManagerBinder) binder);

            // Wait in a separate thread to avoid stalling the UI
            new Thread() {
                @Override
                public void run() {
                    // Wait for the binder to be ready
                    localBinder.waitForReady();

                    // Now make the binder visible
                    managerBinder = localBinder;

                    // Start updates
                    startComputerUpdates();

                    // Force a keypair to be generated early to avoid discovery delays
                    new AndroidCryptoProvider(PcViewActivity.this).getClientCertificate();
                }
            }.start();
        }

        public void onServiceDisconnected(ComponentName className) {
            managerBinder = null;
        }
    };

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

        // Only reinitialize views if completeOnCreate() was called
        // before this callback. If it was not, completeOnCreate() will
        // handle initializing views with the config change accounted for.
        // This is not prone to races because both callbacks are invoked
        // in the main thread.
        if (completeOnCreateCalled) {
            // Reinitialize views just in case orientation changed
            initializeViews();
        }
    }

    private final static int PAIR_ID = 2;
    private final static int UNPAIR_ID = 3;
    private final static int WOL_ID = 4;
    private final static int DELETE_ID = 5;
    private final static int RESUME_ID = 6;
    private final static int QUIT_ID = 7;
    private final static int VIEW_DETAILS_ID = 8;
    private final static int FULL_APP_LIST_ID = 9;
    private final static int TEST_NETWORK_ID = 10;
    private final static int GAMESTREAM_EOL_ID = 11;

    private void initializeViews() {
        setContentView(R.layout.activity_pc_view);

        layoutNoConnect = findViewById(R.id.layoutNoConnect);
        layoutContent = findViewById(R.id.layoutContent);
        //适配刘海屏
        ScreenUtil.adapterDisplayCutout(layoutContent);

        View layoutNotConnect = findViewById(R.id.layoutNotConnect);
        layoutNotConnect.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Settings.ACTION_WIFI_SETTINGS);
                // 判断设备上是否已经有能响应该intent的Activity
                if (intent.resolveActivity(getPackageManager()) != null) {
                    startActivity(intent);
                }
            }
        });

        //UiHelper.notifyNewRootView(this);

        // Allow floating expanded PiP overlays while browsing PCs
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            setShouldDockBigOverlays(false);
        }

        // Set default preferences if we've never been run
        PreferenceManager.setDefaultValues(this, R.xml.preferences, false);

        // Set the correct layout for the PC grid
        //computerAdapter.updateLayoutWithPreferences(this, PreferenceConfiguration.readPreferences(this));

        // Setup the list view
        TextView btnSetting = findViewById(R.id.btnSetting);
        btnSetting.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                //startActivity(new Intent(PcViewActivity.this, StreamSettingsActivity.class));
                startActivity(new Intent(PcViewActivity.this, SettingActivity.class));
            }
        });

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new GridLayoutManager(this, 5));
        GridSpacingItemDecoration itemDecoration = new GridSpacingItemDecoration(
                5,
                ScreenUtil.dp2px(this, 16f),
                true
        );
        recyclerView.addItemDecoration(itemDecoration);
        computerAdapter = new ComputerAdapter(new ComputerAdapter.Listener() {
            @Override
            public void onItemClick(View view, int position, ComputerObject info) {
                ComputerObject computer = (ComputerObject) computerAdapter.getItem(position);
                if (computer.details.state == ComputerDetails.State.UNKNOWN ||
                        computer.details.state == ComputerDetails.State.OFFLINE) {
                    //showItemDialog(info);
                } else if (computer.details.pairState != PairState.PAIRED) {
                    // Pair an unpaired machine by default
                    doPair(computer.details);
                } else {
                    doAppList(computer.details, false, false);
                }
            }

            @Override
            public void onItemLongClick(View view, int position, ComputerObject info) {
                showItemDialog(info);
            }

            @Override
            public void onAddItem() {
                Intent i = new Intent(PcViewActivity.this, AddComputerActivity.class);
                startActivity(i);
                /*AddPcDialog.showDialog(PcViewActivity.this, new AddPcDialog.OnDialogClickListener() {
                    @Override
                    public void onDialogClick(AddPcDialog dialog) {

                    }
                });*/
            }
        });
        recyclerView.setAdapter(computerAdapter);

        computerAdapter.notifyDataSetChanged();


        UiHelper.applyStatusBarPadding(recyclerView);
        registerForContextMenu(recyclerView);

        EventHub.liveData_changeLanguage.observe(this, new Observer<Void>() {
            @Override
            public void onChanged(Void unused) {
                TextView tvTitle = findViewById(R.id.tvTitle);
                TextView tvMirrorDevice = findViewById(R.id.tvMirrorDevice);
                TextView btnSetting = findViewById(R.id.btnSetting);
                tvTitle.setText(LanguageUtils.getString(R.string.Mirror));
                tvMirrorDevice.setText(LanguageUtils.getString(R.string.MirrorDevice));
                btnSetting.setText(LanguageUtils.getString(R.string.Setting));

                computerAdapter.notifyDataSetChanged();
            }
        });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Assume we're in the foreground when created to avoid a race
        // between binding to CMS and onResume()
        inForeground = true;

        // Create a GLSurfaceView to fetch GLRenderer unless we have
        // a cached result already.
        final GlPreferences glPrefs = GlPreferences.readPreferences(this);
        if (!glPrefs.savedFingerprint.equals(Build.FINGERPRINT) || glPrefs.glRenderer.isEmpty()) {
            GLSurfaceView surfaceView = new GLSurfaceView(this);
            surfaceView.setRenderer(new GLSurfaceView.Renderer() {
                @Override
                public void onSurfaceCreated(GL10 gl10, EGLConfig eglConfig) {
                    // Save the GLRenderer string so we don't need to do this next time
                    glPrefs.glRenderer = gl10.glGetString(GL10.GL_RENDERER);
                    glPrefs.savedFingerprint = Build.FINGERPRINT;
                    glPrefs.writePreferences();

                    LogUtil.i("Fetched GL Renderer: " + glPrefs.glRenderer);

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            completeOnCreate();
                        }
                    });
                }

                @Override
                public void onSurfaceChanged(GL10 gl10, int i, int i1) {
                }

                @Override
                public void onDrawFrame(GL10 gl10) {
                }
            });
            setContentView(surfaceView);
        } else {
            LogUtil.i("Cached GL Renderer: " + glPrefs.glRenderer);
            completeOnCreate();
        }
    }

    private void completeOnCreate() {
        completeOnCreateCalled = true;

        shortcutHelper = new ShortcutHelper(this);

        UiHelper.setLocale(this);

        // Bind to the computer manager service
        bindService(new Intent(PcViewActivity.this, ComputerManagerService.class), serviceConnection,
                Service.BIND_AUTO_CREATE);

        initializeViews();
    }

    private void startComputerUpdates() {
        // Only allow polling to start if we're bound to CMS, polling is not already running,
        // and our activity is in the foreground.
        if (managerBinder != null && !runningPolling && inForeground) {
            freezeUpdates = false;
            managerBinder.startPolling(new ComputerManagerListener() {
                @Override
                public void notifyComputerUpdated(final ComputerDetails details) {
                    if (!freezeUpdates) {
                        PcViewActivity.this.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                updateComputer(details);
                            }
                        });

                        // Add a launcher shortcut for this PC (off the main thread to prevent ANRs)
                        if (details.pairState == PairState.PAIRED) {
                            shortcutHelper.createAppViewShortcutForOnlineHost(details);
                        }
                    }
                }
            });
            runningPolling = true;
        }
    }

    private void stopComputerUpdates(boolean wait) {
        if (managerBinder != null) {
            if (!runningPolling) {
                return;
            }

            freezeUpdates = true;

            managerBinder.stopPolling();

            if (wait) {
                managerBinder.waitForPollingStopped();
            }

            runningPolling = false;
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        if (managerBinder != null) {
            unbindService(serviceConnection);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        // Display a decoder crash notification if we've returned after a crash
        UiHelper.showDecoderCrashDialog(this);

        inForeground = true;
        startComputerUpdates();

        App.getContext().getHandler().post(updateWifiStateTask);
    }

    @Override
    protected void onPause() {
        super.onPause();

        inForeground = false;
        stopComputerUpdates(false);

        App.getContext().getHandler().removeCallbacks(updateWifiStateTask);
    }

    @Override
    protected void onStop() {
        super.onStop();

        Dialog.closeDialogs();
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
        stopComputerUpdates(false);

        // Call superclass
        super.onCreateContextMenu(menu, v, menuInfo);

        AdapterContextMenuInfo info = (AdapterContextMenuInfo) menuInfo;
        ComputerObject computer = (ComputerObject) computerAdapter.getItem(info.position);

        // Add a header with PC status details
        menu.clearHeader();
        String headerTitle = computer.details.name + " - ";
        switch (computer.details.state) {
            case ONLINE:
                headerTitle += getResources().getString(R.string.pcview_menu_header_online);
                break;
            case OFFLINE:
                menu.setHeaderIcon(R.drawable.ic_pc_offline);
                headerTitle += getResources().getString(R.string.pcview_menu_header_offline);
                break;
            case UNKNOWN:
                headerTitle += getResources().getString(R.string.pcview_menu_header_unknown);
                break;
        }

        menu.setHeaderTitle(headerTitle);

        // Inflate the context menu
        if (computer.details.state == ComputerDetails.State.OFFLINE ||
                computer.details.state == ComputerDetails.State.UNKNOWN) {
            menu.add(Menu.NONE, WOL_ID, 1, getResources().getString(R.string.pcview_menu_send_wol));
            menu.add(Menu.NONE, GAMESTREAM_EOL_ID, 2, getResources().getString(R.string.pcview_menu_eol));
        } else if (computer.details.pairState != PairState.PAIRED) {
            menu.add(Menu.NONE, PAIR_ID, 1, getResources().getString(R.string.pcview_menu_pair_pc));
            if (computer.details.nvidiaServer) {
                menu.add(Menu.NONE, GAMESTREAM_EOL_ID, 2, getResources().getString(R.string.pcview_menu_eol));
            }
        } else {
            if (computer.details.runningGameId != 0) {
                menu.add(Menu.NONE, RESUME_ID, 1, getResources().getString(R.string.applist_menu_resume));
                menu.add(Menu.NONE, QUIT_ID, 2, getResources().getString(R.string.applist_menu_quit));
            }

            if (computer.details.nvidiaServer) {
                menu.add(Menu.NONE, GAMESTREAM_EOL_ID, 3, getResources().getString(R.string.pcview_menu_eol));
            }

            menu.add(Menu.NONE, FULL_APP_LIST_ID, 4, getResources().getString(R.string.pcview_menu_app_list));
        }

        menu.add(Menu.NONE, TEST_NETWORK_ID, 5, getResources().getString(R.string.pcview_menu_test_network));
        menu.add(Menu.NONE, DELETE_ID, 6, getResources().getString(R.string.pcview_menu_delete_pc));
        menu.add(Menu.NONE, VIEW_DETAILS_ID, 7, getResources().getString(R.string.pcview_menu_details));
    }

    @Override
    public void onContextMenuClosed(Menu menu) {
        // For some reason, this gets called again _after_ onPause() is called on this activity.
        // startComputerUpdates() manages this and won't actual start polling until the activity
        // returns to the foreground.
        startComputerUpdates();
    }

    private void doPair(final ComputerDetails computer) {
        if (computer.state == ComputerDetails.State.OFFLINE || computer.activeAddress == null) {
            Toast.makeText(PcViewActivity.this, getResources().getString(R.string.pair_pc_offline), Toast.LENGTH_SHORT).show();
            return;
        }
        if (managerBinder == null) {
            Toast.makeText(PcViewActivity.this, getResources().getString(R.string.error_manager_not_running), Toast.LENGTH_LONG).show();
            return;
        }

        //Toast.makeText(PcViewActivity.this, getResources().getString(R.string.pairing), Toast.LENGTH_SHORT).show();
        new Thread(new Runnable() {
            @Override
            public void run() {
                NvHTTP httpConn;
                String message;
                boolean success = false;
                try {
                    // Stop updates and wait while pairing
                    stopComputerUpdates(true);

                    httpConn = new NvHTTP(ServerHelper.getCurrentAddressFromComputer(computer),
                            computer.httpsPort, managerBinder.getUniqueId(), computer.serverCert,
                            PlatformBinding.getCryptoProvider(PcViewActivity.this));
                    if (httpConn.getPairState() == PairState.PAIRED) {
                        // Don't display any toast, but open the app list
                        message = null;
                        success = true;
                    } else {
                        final String pinStr = PairingManager.generatePinString();

                        // Spin the dialog off in a thread because it blocks
                        /*Dialog.displayDialog(PcViewActivity.this, getResources().getString(R.string.pair_pairing_title),
                                getResources().getString(R.string.pair_pairing_msg) + " " + pinStr + "\n\n" +
                                        getResources().getString(R.string.pair_pairing_help), false);*/
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                pairPcDialog = PairPcDialog.showDialog(PcViewActivity.this, pinStr, new PairPcDialog.OnDialogClickListener() {
                                    @Override
                                    public void onDialogClick(PairPcDialog dialog) {

                                    }
                                });
                            }
                        });

                        PairingManager pm = httpConn.getPairingManager();

                        PairState pairState = pm.pair(httpConn.getServerInfo(true), pinStr);
                        if (pairState == PairState.PIN_WRONG) {
                            message = getResources().getString(R.string.pair_incorrect_pin);
                        } else if (pairState == PairState.FAILED) {
                            if (computer.runningGameId != 0) {
                                message = getResources().getString(R.string.pair_pc_ingame);
                            } else {
                                message = getResources().getString(R.string.pair_fail);
                            }
                        } else if (pairState == PairState.ALREADY_IN_PROGRESS) {
                            message = getResources().getString(R.string.pair_already_in_progress);
                        } else if (pairState == PairState.PAIRED) {
                            // Just navigate to the app view without displaying a toast
                            message = null;
                            success = true;

                            // Pin this certificate for later HTTPS use
                            managerBinder.getComputer(computer.uuid).serverCert = pm.getPairedCert();

                            // Invalidate reachability information after pairing to force
                            // a refresh before reading pair state again
                            managerBinder.invalidateStateForComputer(computer.uuid);
                        } else {
                            // Should be no other values
                            message = null;
                        }
                    }
                } catch (UnknownHostException e) {
                    message = getResources().getString(R.string.error_unknown_host);
                } catch (FileNotFoundException e) {
                    message = getResources().getString(R.string.error_404);
                } catch (XmlPullParserException | IOException e) {
                    e.printStackTrace();
                    //message = e.getMessage();
                    message = null;
                }

                Dialog.closeDialogs();

                final String toastMessage = message;
                final boolean toastSuccess = success;
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (toastMessage != null) {
                            Toast.makeText(PcViewActivity.this, toastMessage, Toast.LENGTH_LONG).show();
                        }

                        if (toastSuccess) {
                            // Open the app list after a successful pairing attempt
                            doAppList(computer, true, false);
                        } else {
                            // Start polling again if we're still in the foreground
                            startComputerUpdates();
                        }
                    }
                });
            }
        }).start();
    }

    private void doWakeOnLan(final ComputerDetails computer) {
        if (computer.state == ComputerDetails.State.ONLINE) {
            Toast.makeText(PcViewActivity.this, getResources().getString(R.string.wol_pc_online), Toast.LENGTH_SHORT).show();
            return;
        }

        if (computer.macAddress == null) {
            Toast.makeText(PcViewActivity.this, getResources().getString(R.string.wol_no_mac), Toast.LENGTH_SHORT).show();
            return;
        }

        new Thread(new Runnable() {
            @Override
            public void run() {
                String message;
                try {
                    WakeOnLanSender.sendWolPacket(computer);
                    message = getResources().getString(R.string.wol_waking_msg);
                } catch (IOException e) {
                    message = getResources().getString(R.string.wol_fail);
                }

                final String toastMessage = message;
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(PcViewActivity.this, toastMessage, Toast.LENGTH_LONG).show();
                    }
                });
            }
        }).start();
    }

    private void doUnpair(final ComputerDetails computer) {
        if (computer.state == ComputerDetails.State.OFFLINE || computer.activeAddress == null) {
            Toast.makeText(PcViewActivity.this, getResources().getString(R.string.error_pc_offline), Toast.LENGTH_SHORT).show();
            return;
        }
        if (managerBinder == null) {
            Toast.makeText(PcViewActivity.this, getResources().getString(R.string.error_manager_not_running), Toast.LENGTH_LONG).show();
            return;
        }

        Toast.makeText(PcViewActivity.this, getResources().getString(R.string.unpairing), Toast.LENGTH_SHORT).show();
        new Thread(new Runnable() {
            @Override
            public void run() {
                NvHTTP httpConn;
                String message;
                try {
                    httpConn = new NvHTTP(ServerHelper.getCurrentAddressFromComputer(computer),
                            computer.httpsPort, managerBinder.getUniqueId(), computer.serverCert,
                            PlatformBinding.getCryptoProvider(PcViewActivity.this));
                    if (httpConn.getPairState() == PairingManager.PairState.PAIRED) {
                        httpConn.unpair();
                        if (httpConn.getPairState() == PairingManager.PairState.NOT_PAIRED) {
                            message = getResources().getString(R.string.unpair_success);
                        } else {
                            message = getResources().getString(R.string.unpair_fail);
                        }
                    } else {
                        message = getResources().getString(R.string.unpair_error);
                    }
                } catch (UnknownHostException e) {
                    message = getResources().getString(R.string.error_unknown_host);
                } catch (FileNotFoundException e) {
                    message = getResources().getString(R.string.error_404);
                } catch (XmlPullParserException | IOException e) {
                    message = e.getMessage();
                    e.printStackTrace();
                }

                final String toastMessage = message;
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(PcViewActivity.this, toastMessage, Toast.LENGTH_LONG).show();
                    }
                });
            }
        }).start();
    }

    private void doAppList(ComputerDetails computer, boolean newlyPaired, boolean showHiddenGames) {
        if (computer.state == ComputerDetails.State.OFFLINE) {
            Toast.makeText(PcViewActivity.this, getResources().getString(R.string.error_pc_offline), Toast.LENGTH_SHORT).show();
            return;
        }
        if (managerBinder == null) {
            Toast.makeText(PcViewActivity.this, getResources().getString(R.string.error_manager_not_running), Toast.LENGTH_LONG).show();
            return;
        }

        /*NvApp app = new NvApp("app", computer.runningGameId, false);
        ServerHelper.doStart(PcViewActivity.this, app, computer, managerBinder);*/
        //跳转到应用列表页面，获取到桌面应用信息后直接跳转到投屏界面
        Intent i = new Intent(this, AppViewActivity.class);
        i.putExtra(AppViewActivity.NAME_EXTRA, computer.name);
        i.putExtra(AppViewActivity.UUID_EXTRA, computer.uuid);
        i.putExtra(AppViewActivity.NEW_PAIR_EXTRA, newlyPaired);
        i.putExtra(AppViewActivity.SHOW_HIDDEN_APPS_EXTRA, showHiddenGames);
        startActivity(i);

        if(pairPcDialog != null){
            pairPcDialog.dismiss();
        }

        SharedPreferenceUtil.setLastDeviceUuid(computer.uuid);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterContextMenuInfo info = (AdapterContextMenuInfo) item.getMenuInfo();
        final ComputerObject computer = (ComputerObject) computerAdapter.getItem(info.position);
        switch (item.getItemId()) {
            case PAIR_ID:
                doPair(computer.details);
                return true;

            case UNPAIR_ID:
                doUnpair(computer.details);
                return true;

            case WOL_ID:
                doWakeOnLan(computer.details);
                return true;

            case DELETE_ID:
                if (ActivityManager.isUserAMonkey()) {
                    LimeLog.info("Ignoring delete PC request from monkey");
                    return true;
                }
                removeComputer(computer.details);
                /*UiHelper.displayDeletePcConfirmationDialog(this, computer.details, new Runnable() {
                    @Override
                    public void run() {
                        if (managerBinder == null) {
                            Toast.makeText(PcViewActivity.this, getResources().getString(R.string.error_manager_not_running), Toast.LENGTH_LONG).show();
                            return;
                        }
                        removeComputer(computer.details);
                    }
                }, null);*/
                return true;

            case FULL_APP_LIST_ID:
                doAppList(computer.details, false, true);
                return true;

            case RESUME_ID:
                if (managerBinder == null) {
                    Toast.makeText(PcViewActivity.this, getResources().getString(R.string.error_manager_not_running), Toast.LENGTH_LONG).show();
                    return true;
                }

                ServerHelper.doStart(this, new NvApp("app", computer.details.runningGameId, false), computer.details, managerBinder);
                return true;

            case QUIT_ID:
                if (managerBinder == null) {
                    Toast.makeText(PcViewActivity.this, getResources().getString(R.string.error_manager_not_running), Toast.LENGTH_LONG).show();
                    return true;
                }

                // Display a confirmation dialog first
                UiHelper.displayQuitConfirmationDialog(this, new Runnable() {
                    @Override
                    public void run() {
                        ServerHelper.doQuit(PcViewActivity.this, computer.details,
                                new NvApp("app", 0, false), managerBinder, null);
                    }
                }, null);
                return true;

            case VIEW_DETAILS_ID:
                Dialog.displayDialog(PcViewActivity.this, getResources().getString(R.string.title_details), computer.details.toString(), false);
                return true;

            case TEST_NETWORK_ID:
                ServerHelper.doNetworkTest(PcViewActivity.this);
                return true;

            case GAMESTREAM_EOL_ID:
                HelpLauncher.launchGameStreamEolFaq(PcViewActivity.this);
                return true;

            default:
                return super.onContextItemSelected(item);
        }
    }

    private void removeComputer(ComputerDetails details) {
        managerBinder.removeComputer(details);

        new DiskAssetLoader(this).deleteAssetsForComputer(details.uuid);

        // Delete hidden games preference value
        getSharedPreferences(AppViewActivity.HIDDEN_APPS_PREF_FILENAME, MODE_PRIVATE)
                .edit()
                .remove(details.uuid)
                .apply();

        for (int i = 0; i < computerAdapter.getItemCountReally(); i++) {
            ComputerObject computer = (ComputerObject) computerAdapter.getItem(i);

            if (details.equals(computer.details)) {
                // Disable or delete shortcuts referencing this PC
                shortcutHelper.disableComputerShortcut(details,
                        getResources().getString(R.string.scut_deleted_pc));

                computerAdapter.removeItem(i);
                computerAdapter.notifyDataSetChanged();

                if (computerAdapter.getItemCountReally() == 0) {
                }

                break;
            }
        }
    }

    private void updateComputer(ComputerDetails details) {
        ComputerObject existingEntry = null;

        for (int i = 0; i < computerAdapter.getItemCountReally(); i++) {
            ComputerObject computer = (ComputerObject) computerAdapter.getItem(i);

            // Check if this is the same computer
            if (details.uuid.equals(computer.details.uuid)) {
                existingEntry = computer;
                break;
            }
        }

        if (existingEntry != null) {
            // Replace the information in the existing entry
            existingEntry.details = details;
        } else {
            // Add a new entry
            computerAdapter.addItem(new ComputerObject(details));
        }

        // Notify the view that the data has changed
        computerAdapter.notifyDataSetChanged();
    }

    @Override
    public int getAdapterFragmentLayoutId() {
        return R.layout.pc_grid_view;
    }

    @Override
    public void receiveAbsListView(AbsListView listView) {
        /*listView.setAdapter(computerAdapter);
        listView.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int pos,
                                    long id) {
                ComputerObject computer = (ComputerObject) computerAdapter.getItem(pos);
                if (computer.details.state == ComputerDetails.State.UNKNOWN ||
                        computer.details.state == ComputerDetails.State.OFFLINE) {
                    // Open the context menu if a PC is offline or refreshing
                    openContextMenu(arg1);
                } else if (computer.details.pairState != PairState.PAIRED) {
                    // Pair an unpaired machine by default
                    doPair(computer.details);
                } else {
                    doAppList(computer.details, false, false);
                }
            }
        });
        UiHelper.applyStatusBarPadding(listView);
        registerForContextMenu(listView);*/
    }

    private void showItemDialog(ComputerObject info) {
        BottomMenuDialog.showDialog(PcViewActivity.this, new BottomMenuDialog.OnConfirmSelectListener() {
            @Override
            public void onConfirmSelect(int type) {
                switch (type) {
                    case 1: {
                        //删除电脑
                        if (ActivityManager.isUserAMonkey()) {
                            LimeLog.info("Ignoring delete PC request from monkey");
                            return;
                        }
                        if (managerBinder == null) {
                            Toast.makeText(PcViewActivity.this, getResources().getString(R.string.error_manager_not_running), Toast.LENGTH_LONG).show();
                            return;
                        }
                        removeComputer(info.details);
                    }
                    break;
                }
            }
        });
    }

    public static class ComputerObject {
        public ComputerDetails details;

        public ComputerObject(ComputerDetails details) {
            if (details == null) {
                throw new IllegalArgumentException("details must not be null");
            }
            this.details = details;
        }

        @Override
        public String toString() {
            return details.name;
        }
    }

    private Runnable updateWifiStateTask = new Runnable() {
        @Override
        public void run() {
            WifiManager wifiManager = (WifiManager) App.getContext().getApplicationContext().getSystemService(Context.WIFI_SERVICE);
            if (wifiManager != null) {
                boolean isWifiEnable = wifiManager.isWifiEnabled();
                WifiInfo wifiInfo = wifiManager.getConnectionInfo();
                if (isWifiEnable /*&& wifiInfo != null && !TextUtils.isEmpty(wifiInfo.getSSID())*/) {
                    if (layoutNoConnect != null) {
                        layoutNoConnect.setVisibility(View.GONE);
                        layoutContent.setVisibility(View.VISIBLE);
                    }
                } else {
                    if (layoutNoConnect != null) {
                        layoutNoConnect.setVisibility(View.VISIBLE);
                        layoutContent.setVisibility(View.GONE);
                    }
                }
            }
            App.getContext().getHandler().postDelayed(updateWifiStateTask, 2000);
        }
    };
}
