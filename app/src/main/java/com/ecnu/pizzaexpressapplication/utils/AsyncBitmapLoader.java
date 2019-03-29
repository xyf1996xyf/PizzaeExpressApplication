package com.ecnu.pizzaexpressapplication.utils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.Message;
import android.widget.ImageView;

import java.io.*;
import java.lang.ref.SoftReference;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;

public class AsyncBitmapLoader {

    private static final String path = "http://pizza-express.oss-cn-shanghai.aliyuncs.com";
    /**
     * 内存图片软引用缓冲
     */
    private HashMap<String, SoftReference<Bitmap>> imageCache = null;

    public static AsyncBitmapLoader asyncBitmapLoader = new AsyncBitmapLoader();

    public AsyncBitmapLoader() {
        imageCache = new HashMap<String, SoftReference<Bitmap>>();
    }

    public Bitmap loadBitmap(final ImageView imageView, final String imageURL, final int reqWidth, final int reqHeight, final ImageCallBack imageCallBack) {
        if (imageURL == null || imageURL.equals("")) {
            return null;
        }
        //在内存缓存中，则返回Bitmap对象     
        if (imageCache.containsKey(imageURL)) {
            SoftReference<Bitmap> reference = imageCache.get(imageURL);
            Bitmap bitmap = reference.get();
            if (bitmap != null) {
                return bitmap;
            }
        } else {
            /**
             * 加上一个对本地缓存的查找   
             */
            String bitmapName = imageURL.substring(imageURL.lastIndexOf("/") + 1);
            File cacheDir = imageView.getContext().getCacheDir();
            File[] cacheFiles = cacheDir.listFiles();
            int i = 0;
            if (null != cacheFiles) {
                for (; i < cacheFiles.length; i++) {
                    if (bitmapName.equals(cacheFiles[i].getName())) {
                        break;
                    }
                }

                if (i < cacheFiles.length) {
                    return BitmapFactory.decodeFile(cacheDir.getPath() + "/" + bitmapName);
                }
            }
        }

        final Handler handler = new Handler() {
            /* (non-Javadoc)   
             * @see android.os.Handler#handleMessage(android.os.Message)   
             */
            @Override
            public void handleMessage(Message msg) {
                // TODO Auto-generated method stub    
                imageCallBack.imageLoad(imageView, (Bitmap) msg.obj);
            }
        };

        //如果不在内存缓存中，也不在本地（被jvm回收掉），则开启线程下载图片    
        new Thread() {
            /* (non-Javadoc)   
             * @see java.lang.Thread#run()   
             */
            @Override
            public void run() {
                // TODO Auto-generated method stub
                Bitmap bitmap;
                try {
                    bitmap = getBitmapByAddress(imageURL, reqWidth, reqHeight);
                } catch (IOException e) {
                    bitmap = null;
                }
                //Bitmap bitmap = dishesDAO.getBitmapByAddress(imageURL,reqWidth,reqWidth);
                imageCache.put(imageURL, new SoftReference<Bitmap>(bitmap));
                Message msg = handler.obtainMessage(0, bitmap);
                handler.sendMessage(msg);

                File dir = imageView.getContext().getCacheDir();
                if (!dir.exists()) {
                    dir.mkdirs();
                }

                File bitmapFile = new File(imageView.getContext().getCacheDir().getPath() + "/" +
                        imageURL.substring(imageURL.lastIndexOf("/") + 1));
                if (!bitmapFile.exists()) {
                    try {
                        bitmapFile.createNewFile();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                FileOutputStream fos;
                try {
                    fos = new FileOutputStream(bitmapFile);
                    bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos);
                    fos.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }.start();

        return null;
    }

    public Bitmap loadBitmap(final ImageView imageView, final String imageURL, final ImageCallBack imageCallBack) {
        int reqWidth = imageView.getLayoutParams().width;
        int reqHeight = imageView.getLayoutParams().height;
        return loadBitmap(imageView, imageURL, reqWidth, reqHeight, imageCallBack);
    }

    public interface ImageCallBack {
        public void imageLoad(ImageView imageView, Bitmap bitmap);
    }

    public static int calculateInSampleSize(BitmapFactory.Options options,
                                            int reqWidth, int reqHeight) {
        // 源图片的高度和宽度
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;
        if (height > reqHeight || width > reqWidth) {
            // 计算出实际宽高和目标宽高的比率
            final int heightRatio = Math.round((float) height / (float) reqHeight);
            final int widthRatio = Math.round((float) width / (float) reqWidth);
            // 选择宽和高中最小的比率作为inSampleSize的值，这样可以保证最终图片的宽和高
            // 一定都会大于等于目标的宽和高。
            if (heightRatio < 0) {
                inSampleSize = widthRatio;
            } else if (widthRatio < 0) {
                inSampleSize = heightRatio;
            } else {
                inSampleSize = heightRatio < widthRatio ? heightRatio : widthRatio;
            }
        }
        return inSampleSize;
    }

    public static Bitmap getBitmapByAddress(String address, int reqWidth, int reqHeight) throws IOException {
        URL url = new URL(path + "/" + address);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        //设置超时时间为6000毫秒，conn.setConnectionTiem(0);表示没有时间限制
        connection.setConnectTimeout(6000);
        //连接设置获得数据流
        connection.setDoInput(true);
        //不使用缓存
        connection.setUseCaches(false);
        //得到数据流
        InputStream is = connection.getInputStream();
        byte[] data = readStream(is);
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeByteArray(data, 0, data.length, options);
        int inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);
        System.out.println(inSampleSize);
        options.inSampleSize = inSampleSize;
        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeByteArray(data, 0, data.length, options);
    }

    public static byte[] readStream(InputStream inStream) throws IOException {
        byte[] buffer = new byte[1024];
        int len = -1;
        ByteArrayOutputStream outStream = new ByteArrayOutputStream();
        while ((len = inStream.read(buffer)) != -1) {
            outStream.write(buffer, 0, len);
        }
        byte[] data = outStream.toByteArray();
        outStream.close();
        inStream.close();
        return data;
    }
}  