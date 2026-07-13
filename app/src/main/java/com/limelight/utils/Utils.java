package com.limelight.utils;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Rect;
import android.media.Image;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.renderscript.Allocation;
import android.renderscript.Element;
import android.renderscript.RenderScript;
import android.renderscript.ScriptIntrinsicYuvToRGB;
import android.renderscript.Type;
import android.util.Log;

import com.limelight.App;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.ByteBuffer;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class Utils {

    /**
     * Convert byte[] to hex string
     *
     * @param src byte[] data
     * @return hex string
     */
    private static String TAG = "NetCamera";

    static final int imageW = 1280;
    static final int imageH = 800;

    static int[] rgba = new int[imageW*imageH];

    public static void setRgba(int w, int h){
        rgba = new int[w*h];
    }

    public static String bytesToHexString(byte[] src){
        StringBuilder stringBuilder = new StringBuilder();
        if (src == null || src.length <= 0) {
            return null;
        }
        for (int i = 0; i < src.length; i++) {
            int v = src[i] & 0xFF;
            String hv = Integer.toHexString(v);
            if (hv.length() < 2) {
                stringBuilder.append(0);
            }
            stringBuilder.append(hv);
        }
        return stringBuilder.toString();
    }

    public static String byteToHex(byte src){
        StringBuilder stringBuilder = new StringBuilder();
        int v = src & 0xFF;
        String hv = Integer.toHexString(v);
        stringBuilder.append("0x");
        if (hv.length() < 2) {
            stringBuilder.append(0);
        }
        stringBuilder.append(hv);
        return stringBuilder.toString();
    }

    public static byte[] readFile(String path){
        FileInputStream fileInputStream;
        try {
            fileInputStream = new FileInputStream(path);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return null;
        }
        ByteArrayOutputStream byteArray = new ByteArrayOutputStream();
        byte[] byteBufferString = new byte[1024];
        try {
            for (int readNum; (readNum = fileInputStream.read(byteBufferString)) != -1; ) {
                byteArray.write(byteBufferString, 0, readNum);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return byteArray.toByteArray();
    }

    public static boolean isFingo(){
        return "FinGo".equals(Build.MODEL);
    }

    public static Bitmap YUV_420_toRGB8888Intrinsics(Image image, Context context) {
        if (image == null) return null;
        int W;
        int H;
        if(isFingo()){
            Rect crop = image.getCropRect();
            W = crop.width();
            H = crop.height();
        }else{
            W = image.getWidth();
            H = image.getHeight();
        }

        Image.Plane Y = image.getPlanes()[0];
        Image.Plane U = image.getPlanes()[1];
        Image.Plane V = image.getPlanes()[2];

        int Yb = Y.getBuffer().remaining();
        int Ub = U.getBuffer().remaining();
        int Vb = V.getBuffer().remaining();
        int offset = 0;//Yb - W*H;
        byte[] data = new byte[Yb + Ub + Vb - offset];
        Y.getBuffer().get(data, 0, Yb);
        V.getBuffer().get(data, Yb-offset, Vb);
        U.getBuffer().get(data, Yb + Vb-offset, Ub);

        RenderScript rs = RenderScript.create(context);
        ScriptIntrinsicYuvToRGB yuvToRgbIntrinsic = ScriptIntrinsicYuvToRGB.create(rs, Element.U8_4(rs));

        Type.Builder yuvType = new Type.Builder(rs, Element.U8(rs)).setX(data.length);
        Allocation in = Allocation.createTyped(rs, yuvType.create(), Allocation.USAGE_SCRIPT);

        Type.Builder rgbaType = new Type.Builder(rs, Element.RGBA_8888(rs)).setX(W).setY(H);
        Allocation out = Allocation.createTyped(rs, rgbaType.create(), Allocation.USAGE_SCRIPT);

        final Bitmap bmpout = Bitmap.createBitmap(W, H, Bitmap.Config.ARGB_8888);
        in.copyFromUnchecked(data);
        yuvToRgbIntrinsic.setInput(in);
        yuvToRgbIntrinsic.forEach(out);
        out.copyTo(bmpout);
        image.close();

        return bmpout ;
    }

    public static Bitmap YUV_420ByteArray_toRGB8888_Bitmap(byte[] data, int width, int height) {

        if(true){
            LogUtil.i("Utils", "YUV_420ByteBuffer_toRGB8888_Bitmap width:" + width + ",height:"+height+"data.length:"+data.length);
            if(width * height * 3 / 2 <= data.length)
            {
                int frameSize = width * height;
//                long lastTime = System.currentTimeMillis();
                for (int i = 0; i < height; i++) {
                    for (int j = 0; j < width; j++) {
                        int y = (0xff & ((int) data[i * width + j]));
                        int u = (0xff & ((int) data[frameSize                    + (i / 2) * (width / 2)  + j / 2]));
                        int v = (0xff & ((int) data[frameSize + (frameSize >> 2) + (i / 2) * (width / 2)  + j / 2]));
                        int r = Math.round(1.164f * (y - 16) + 1.596f * (v - 128));
                        int g = Math.round(1.164f * (y - 16) - 0.813f * (v - 128) - 0.391f * (u - 128));
                        int b = Math.round(1.164f * (y - 16) + 2.018f * (u - 128));
                        r = r < 0 ? 0 : (r > 255 ? 255 : r);
                        g = g < 0 ? 0 : (g > 255 ? 255 : g);
                        b = b < 0 ? 0 : (b > 255 ? 255 : b);
                        rgba[i * width + j] = 0xff000000 + (r << 16) + (g << 8) + b;
                    }
                }
//                long curTime = System.currentTimeMillis();
//                long costTime = curTime - lastTime;
//                LogUtil.i(TAG, "YUV_420ByteArray_toRGB8888_Bitmap1 cost " + costTime + "ms");
//
//                lastTime = curTime;
                Bitmap bmp = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
                bmp.setPixels(rgba, 0 , width, 0, 0, width, height);
//                curTime = System.currentTimeMillis();
//                costTime = curTime - lastTime;
//                LogUtil.i(TAG, "YUV_420ByteArray_toRGB8888_Bitmap2 cost " + costTime + "ms");
                return bmp;
            }
            else{
                return null;
            }
        }
        return null;
    }

    public static Bitmap YUV_420ByteBuffer_toRGB8888_Bitmap(ByteBuffer buffer, int width, int height) {

        if(true){
//            LogUtil.i("Utils", "YUV_420ByteBuffer_toRGB8888_Bitmap width:" + width + ",height:"+height);
            if(width * height * 3 / 2 <= buffer.limit())
            {
                byte[] data = new byte[buffer.limit()];
                buffer.get(data, 0, buffer.limit());
                int frameSize = width * height;

                int[] rgba = new int[frameSize];
                for (int i = 0; i < height; i++) {
                    for (int j = 0; j < width; j++) {
                        int y = (0xff & ((int) data[i * width + j]));
                        int u = (0xff & ((int) data[frameSize                    + (i / 2) * (width / 2)  + j / 2]));
                        int v = (0xff & ((int) data[frameSize + (frameSize >> 2) + (i / 2) * (width / 2)  + j / 2]));
                        int r = Math.round(1.164f * (y - 16) + 1.596f * (v - 128));
                        int g = Math.round(1.164f * (y - 16) - 0.813f * (v - 128) - 0.391f * (u - 128));
                        int b = Math.round(1.164f * (y - 16) + 2.018f * (u - 128));
                        r = r < 0 ? 0 : (r > 255 ? 255 : r);
                        g = g < 0 ? 0 : (g > 255 ? 255 : g);
                        b = b < 0 ? 0 : (b > 255 ? 255 : b);
                        rgba[i * width + j] = 0xff000000 + (r << 16) + (g << 8) + b;
                    }
                }
                Bitmap bmp = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
                bmp.setPixels(rgba, 0 , width, 0, 0, width, height);
                return bmp;
            }
            else{
                return null;
            }
        }
        return null;
    }

    public static Bitmap YUV_420SPByteBuffer_toRGB8888_Bitmap(ByteBuffer buffer, int width, int height) {

        if(true){
//            LogUtil.i("Utils", "YUV_420SPByteBuffer_toRGB8888_Bitmap width:" + width + ",height:"+height);
            if(width * height * 3 / 2 <= buffer.limit())
            {
                byte[] data = new byte[buffer.limit()];
                buffer.get(data, 0, buffer.limit());
                int frameSize = width * height;

                int[] rgba = new int[frameSize];
                for (int i = 0; i < height; i++) {
                    for (int j = 0; j < width; j++) {
                        int y = (0xff & ((int) data[i * width + j]));
                        int u = (0xff & ((int) data[frameSize + (i / 2) * width  + j / 2 * 2 + 0]));
                        int v = (0xff & ((int) data[frameSize + (i / 2) * width  + j / 2 * 2 + 1]));
                        int r = Math.round(1.164f * (y - 16) + 1.596f * (v - 128));
                        int g = Math.round(1.164f * (y - 16) - 0.813f * (v - 128) - 0.391f * (u - 128));
                        int b = Math.round(1.164f * (y - 16) + 2.018f * (u - 128));
                        r = r < 0 ? 0 : (r > 255 ? 255 : r);
                        g = g < 0 ? 0 : (g > 255 ? 255 : g);
                        b = b < 0 ? 0 : (b > 255 ? 255 : b);
                        rgba[i * width + j] = 0xff000000 + (r << 16) + (g << 8) + b;
                    }
                }
                Bitmap bmp = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
                bmp.setPixels(rgba, 0 , width, 0, 0, width, height);
                return bmp;
            }
            else{
                LogUtil.e(TAG, "buffer length(" + buffer.limit() + ") incorrect! width:" + width + ",height:" + height);
                return null;
            }
        }
        return null;
    }

    public static  boolean   haveNet(Context context) {
        // 获得网络状态管理器
        ConnectivityManager      connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivityManager == null) {
            return false;
        }
        else {
            NetworkInfo[] net_info = connectivityManager.getAllNetworkInfo();
            if (net_info != null) {
                for (int i = 0; i < net_info.length; i++) {
                    // 判断获得的网络状态是否是处于连接状态
                    if (net_info[i].getState() == NetworkInfo.State.CONNECTED) {
                        return true;
                    }
                }
            }
        }
        return false;
    }



    public static String getVersionName(Context context){
        String pkName = context.getPackageName();
        try {
            return context.getPackageManager().getPackageInfo(pkName, 0).versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            return "";
        }
    }
    public static void compress(String source, ZipOutputStream zos, String name) throws Exception{
        File sourceFile = new File(source);
        compress(sourceFile, zos, name);
    }
    public static void compress(File sourceFile, ZipOutputStream zos, String name) throws Exception{
        if(!sourceFile.exists())
            return;

        byte[] buf = new byte[10240];
        if(sourceFile.isFile()){
            // 向zip输出流中添加一个zip实体，构造器中name为zip实体的文件的名字
            zos.putNextEntry(new ZipEntry(name));
            // copy文件到zip输出流中
            int len;
            FileInputStream in = new FileInputStream(sourceFile);
            while ((len = in.read(buf)) != -1){
                zos.write(buf, 0, len);
            }
            // Complete the entry
            zos.closeEntry();
            in.close();
        } else {
            File[] listFiles = sourceFile.listFiles();
            if(listFiles == null || listFiles.length == 0){
                // 空文件夹的处理
                zos.putNextEntry(new ZipEntry(name + "/"));
                // 没有文件，不需要文件的copy
                zos.closeEntry();
            }else {
                for (File file : listFiles) {
                    // 注意：file.getName()前面需要带上父文件夹的名字加一斜杠,
                    // 不然最后压缩包中就不能保留原来的文件结构,即：所有文件都跑到压缩包根目录下了
                    compress(file, zos, name + "/" + file.getName());
                }
            }
        }
    }

    public static  String httpGet(String urlStr){
        LogUtil.d(TAG, urlStr);
        InputStream inputStream;
        try {
            URL url = new URL(urlStr);
            HttpURLConnection httpUrlConn = (HttpURLConnection) url.openConnection();
            httpUrlConn.setDoInput(true);
            httpUrlConn.setRequestMethod("GET");
            httpUrlConn.connect();
            // 获得返回的输入流
            inputStream = httpUrlConn.getInputStream();
            // 定义 BufferedReader输入流来读取URL的响应
            BufferedReader in = new BufferedReader(new InputStreamReader(inputStream));
            String line;
            StringBuilder result = new StringBuilder();
            while ((line = in.readLine()) != null) {
                result.append(line);
            }
            return result.toString();
        } catch (Exception e) {
            LogUtil.d(TAG, e.toString());
            e.printStackTrace();
        }
        return "";
    }

    public  static synchronized String httpGetMini(String urlStr){
        LogUtil.d(TAG, urlStr);
        Log.e("ScanActivity","urlStr="+urlStr);
        InputStream inputStream;
        try {
            URL url = new URL(urlStr);
            HttpURLConnection httpUrlConn = (HttpURLConnection) url.openConnection();
            httpUrlConn.setDoInput(true);
            httpUrlConn.setRequestMethod("GET");
            httpUrlConn.connect();
            // 获得返回的输入流
            inputStream = httpUrlConn.getInputStream();
            // 定义 BufferedReader输入流来读取URL的响应
            BufferedInputStream inputStream1 = new BufferedInputStream(inputStream);
            byte[] lenth = new byte[60];
            inputStream1.read(lenth);
            int size=(lenth[0]& 0xff)+ (lenth[1]& 0xff)*256+ (lenth[2]& 0xff)*256*256+(lenth[3] & 0xff)*256*256*256 ;
            Log.e("ScanActivity","size----------"+size);
            float result = 0.1f;
            if(size>0){
                result=(float) (1.0/size);
            }
            return ""+result;
        } catch (Exception e) {
            LogUtil.d(TAG, e.toString());
            e.printStackTrace();
        }
        return "";
    }

    /**
     * 根据手机的分辨率从 dp 的单位 转成为 px(像素)
     */
    public static int dpToPx(final Context context, final float dp) {
        return (int) (dp * context.getResources().getDisplayMetrics().density);
    }

    /**
     * 获取应用版本号
     * @return
     */
    public static String getAppVersionName(){
        PackageManager packageManager = App.getContext().getPackageManager();
        PackageInfo packInfo;
        try {
            packInfo = packageManager.getPackageInfo(App.getContext().getPackageName(),0);
            String versionName = packInfo.versionName;
            //String versionCode = packInfo.versionCode;
            return versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        return "";
    }
}

