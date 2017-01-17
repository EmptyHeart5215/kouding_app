package beihua.example.com.kouding_app.utils;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.ParcelFileDescriptor;
import android.provider.MediaStore;

import java.io.FileDescriptor;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;

import beihua.example.com.kouding_app.MainActivity;
import beihua.example.com.kouding_app.R;
import beihua.example.com.kouding_app.bean.Mp3Info;

/**
 * Created by 李明 on 2017/1/11.
 */
public class MediaUtils {

    //获取封面专辑url
    private static final Uri albumArtUri = Uri.parse("content://media/external/audio/albumart");


    /**
     * 根据歌曲id查询歌曲信息
     *
     * @param context
     * @param _id
     * @return
     */
    public static Mp3Info getMp3Info(Context context, long _id) {

        Cursor cursor = context.getContentResolver().query(

                MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, null,

                MediaStore.Audio.Media._ID + "=" + _id, null,

                MediaStore.Audio.Media.DEFAULT_SORT_ORDER);
        Mp3Info mp3info = null;

        if (cursor.moveToNext()) {

            mp3info = new Mp3Info();
            long id = cursor.getLong(cursor.getColumnIndex(MediaStore.Audio.Media._ID));
            String title = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.TITLE));//音乐标题
            String artList = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ARTIST)); //艺术家
            String album = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ALBUM)); //专辑
            long albumId = cursor.getLong(cursor.getColumnIndex(MediaStore.Audio.Media.ALBUM_ID));

            long duration = cursor.getLong(cursor.getColumnIndex(MediaStore.Audio.Media.DURATION));//市场
            long size = cursor.getLong(cursor.getColumnIndex(MediaStore.Audio.Media.SIZE));//文件大小
            String url = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DATA)); //文件路劲
            int isMusic = cursor.getInt(cursor.getColumnIndex(MediaStore.Audio.Media.IS_MUSIC)); //是否为音乐


            if (isMusic != 0) { //只把音乐添加到集合中
                mp3info.setId(id);
                mp3info.setTitle(title);
                mp3info.setArList(artList);
                mp3info.setAlbum(album);
                mp3info.setAlbumId(albumId);
                mp3info.setDuration(duration);
                mp3info.setSize(size);
                mp3info.setUrl(url);


            }

        }
        cursor.close();
        return mp3info;
    }

    /**
     * 用于从数据库中查询歌曲信息，保存在list当中
     *
     * @param context
     * @return
     */

    public static long[] getMp3InfoIds(Context context) {
        Cursor cursor = context.getContentResolver().query(
                MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                new String[]{MediaStore.Audio.Media._ID},
                MediaStore.Audio.Media.DURATION + ">=180000", null,
                MediaStore.Audio.Media.DEFAULT_SORT_ORDER);
        long[] ids = null;
        if (cursor != null) {
            ids = new long[cursor.getCount()];
            for (int i = 0; i < cursor.getCount(); i++) {
                cursor.moveToNext();
                ids[i] = cursor.getLong(0);
            }
        }

        cursor.close();
        return ids;

    }

    /**
     * 用于从数据库中查询歌曲信息，保存在list当中
     *
     * @param context
     * @return
     */
    public static ArrayList<Mp3Info> getMp3Infos(Context context) {

        Cursor cursor = context.getContentResolver().query(
                MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, null,
                MediaStore.Audio.Media.DURATION + ">=180000", null, //歌曲长度大于180000，也就是两分钟才算歌曲
                MediaStore.Audio.Media.DEFAULT_SORT_ORDER);

        ArrayList<Mp3Info> mp3Infos = new ArrayList<>();

        for (int i = 0; i < cursor.getCount(); i++) {

            cursor.moveToNext();
            Mp3Info mp3Info = new Mp3Info();

            long id = cursor.getLong(cursor.getColumnIndex(MediaStore.Audio.Media._ID));
            String title = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.TITLE));//音乐标题
            String artList = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ARTIST)); //艺术家
            String album = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ALBUM)); //专辑
            long albumId = cursor.getLong(cursor.getColumnIndex(MediaStore.Audio.Media.ALBUM_ID));

            long duration = cursor.getLong(cursor.getColumnIndex(MediaStore.Audio.Media.DURATION));//市场
            long size = cursor.getLong(cursor.getColumnIndex(MediaStore.Audio.Media.SIZE));//文件大小
            String url = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DATA)); //文件路劲
            int isMusic = cursor.getInt(cursor.getColumnIndex(MediaStore.Audio.Media.IS_MUSIC)); //是否为音乐
            if (isMusic != 0) { //只把音乐添加到集合中
                mp3Info.setId(id);
                mp3Info.setTitle(title);
                mp3Info.setArList(artList);
                mp3Info.setAlbum(album);
                mp3Info.setAlbumId(albumId);
                mp3Info.setDuration(duration);
                mp3Info.setSize(size);
                mp3Info.setUrl(url);
                //添加到集合当中
                mp3Infos.add(mp3Info);

            }
        }

        cursor.close();
        return mp3Infos;
    }

    /**
     * 格式化时间，将毫秒转化成分：秒格式
     *
     * @param time
     * @return
     */
    public static String formatTime(long time) {
        String min = time / (1000 * 60) + "";
        String sec = time % (1000 * 60) + "";

        if (min.length() < 2) {
            min = "0" + time / (1000 * 60) + "";
        } else {

            min = time / (1000 * 60) + "";
        }


        if (sec.length() == 4) {
            sec = "0" + (time % (1000 * 60)) + "";
        } else if (sec.length() == 3) {
            sec = "00" + (time % (1000 * 60)) + "";

        } else if (sec.length() == 2) {
            sec = "000" + (time % (1000 * 60)) + "";
        } else if (sec.length() == 1) {
            sec = "0000" + (time % (1000 * 60)) + "";
        }

        return min + ":" + sec.trim().substring(0, 2);

    }

    /**
     * 获取默认专辑图片
     *
     * @param context
     * @param small
     * @return
     */
    public static Bitmap getDefaultArtWork(Context context, boolean small) {
        BitmapFactory.Options opts = new BitmapFactory.Options();
        opts.inPreferredConfig = Bitmap.Config.RGB_565;
        if (small) { //返回小图片

            return BitmapFactory.decodeStream(context.getResources().openRawResource(R.mipmap.notification_icon), null, opts);

        }
        return BitmapFactory.decodeStream(context.getResources().openRawResource(R.mipmap.notification_icon), null, opts);

    }

    /**
     * 从文件当中获取当前专辑封面图片
     *
     * @param context
     * @param songid
     * @param albumid
     * @return
     */


    private static Bitmap getArtworkFromFile(Context context, long songid, long albumid) {

        Bitmap bm = null;
        if (albumid < 0 && songid < 0) {
            throw new IllegalArgumentException(" must specify an album a song id");


        }
        try {

            BitmapFactory.Options options = new BitmapFactory.Options();
            FileDescriptor fd = null;
            if (albumid < 0) {
                Uri uri = Uri.parse("content://media/external/audio/media/" + songid + "/albumart");

                ParcelFileDescriptor pfd = context.getContentResolver().openFileDescriptor(uri, "r");
                if (pfd != null) {
                    fd = pfd.getFileDescriptor();
                }

            } else {
                Uri uri = ContentUris.withAppendedId(albumArtUri, albumid);
                ParcelFileDescriptor pfd = context.getContentResolver().openFileDescriptor(uri, "r");
                if (pfd != null) {
                    fd = pfd.getFileDescriptor();
                }


            }


            options.inSampleSize = 1;
            //只进行大小判断
            options.inJustDecodeBounds = true;
            //调用此方法得到options得到图片大小
            BitmapFactory.decodeFileDescriptor(fd, null, options);
            //我们的目标实在800pixel的画面上显示，所以需要调用computeSampleSize得到图片的缩放比例
            options.inDensity = 100;

            //得到了缩放比例，开始读入bitmap数据
            options.inJustDecodeBounds = false;
            options.inDither = false;
            options.inPreferredConfig = Bitmap.Config.ARGB_8888;

            //根据options参数，减少所需要的内存
            bm = BitmapFactory.decodeFileDescriptor(fd, null, options);


        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }


        return bm;

    }

    /**
     * 获取专辑封面位图对象
     */
    public static Bitmap getArtwork(Context context, long song_id, long album_id, boolean allowdefalut, boolean small) {
        if (album_id < 0) {
            if (song_id < 0) {
                Bitmap bm = getArtworkFromFile(context, song_id, -1);
                if (bm != null) {
                    return bm;
                }
            }
            if (allowdefalut) {
                return getDefaultArtWork(context, small);
            }
            return null;
        }

        ContentResolver res = context.getContentResolver();
        Uri uri = ContentUris.withAppendedId(albumArtUri, album_id);
        if (uri != null) {

            InputStream in = null;

            try {
                in = res.openInputStream(uri);
                BitmapFactory.Options options = new BitmapFactory.Options();
                //先指定原始大小
                options.inSampleSize = 1;
                //只进行大小判断
                options.inJustDecodeBounds = true;

                //调用此方法得到options得到图片大小
                BitmapFactory.decodeStream(in, null, options);
                /**我们的目标是在你n pixel 的画面上显示。所以需要调用computeSampleSize的图片的缩放比例**/
                /** 这里的target为800 是根据默认专辑图片大小决定的，800只是测试数字，但是试验后发现完美结合**/

                if (small) {
                    options.inSampleSize = computeSampleSize(options,-1, 48*48);
                } else {
                    options.inSampleSize = computeSampleSize(options, -1,48*48);
                }

                //我们得到了缩放比例。现在开始正式读入bitmap数据

                options.inJustDecodeBounds = false;
                options.inDither = false;
                options.inPreferredConfig = Bitmap.Config.ARGB_8888;
                in = res.openInputStream(uri);
                return BitmapFactory.decodeStream(in, null, options);

            } catch (FileNotFoundException e) {
                Bitmap bm = getArtworkFromFile(context, song_id, album_id);
                if (bm != null) {
                    if (bm.getConfig() == null) {
                        bm = bm.copy(Bitmap.Config.RGB_565, false);
                        if (bm == null && allowdefalut) {
                            return getDefaultArtWork(context, small);
                        }
                    }
                } else if (allowdefalut) {
                    bm = getDefaultArtWork(context, small);
                }

                return bm;

            } finally {
                try {
                    if (in != null) {
                        in.close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        return null;
    }

    public static int computeSampleSize(BitmapFactory.Options options, int minSideLength, int maxNumOfPixels) {
        int initialSize = computeInitialSampleSize(options, minSideLength, maxNumOfPixels);
        int roundedSize;
        if (initialSize <= 8) {
            roundedSize = 1;
            while (roundedSize < initialSize) {
                roundedSize <<= 1;
            }
        } else {
            roundedSize = (initialSize + 7) / 8 * 8;
        }
        return roundedSize;
    }

    private static int computeInitialSampleSize(BitmapFactory.Options options, int minSideLength, int maxNumOfPixels) {
        double w = options.outWidth;
        double h = options.outHeight;
        int lowerBound = (maxNumOfPixels == -1) ? 1 : (int) Math.ceil(Math.sqrt(w * h / maxNumOfPixels));
        int upperBound = (minSideLength == -1) ? 128 : (int) Math.min(Math.floor(w / minSideLength), Math.floor(h / minSideLength));
        if (upperBound < lowerBound) {
            // return the larger one when there is no overlapping zone.
            return lowerBound;
        }
        if ((maxNumOfPixels == -1) && (minSideLength == -1)) {
            return 1;
        } else if (minSideLength == -1) {
            return lowerBound;
        } else {
            return upperBound;
        }


    }
}