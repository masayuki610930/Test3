package com.example.masayuki.test3;


import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.ImageFormat;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.YuvImage;
import android.hardware.Camera;
import android.os.Environment;
import android.os.Vibrator;
import android.provider.MediaStore;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;



/**
 * Created by Masayuki_2 on 2014/11/05.
 */
public class CameraView extends SurfaceView implements SurfaceHolder.Callback, Camera.PictureCallback {

    private Camera myCamera;

    private SurfaceHolder holder = null;
    private static final String folderName = "CameraView";
    private Context myContext = null;
    private SurfaceView surfaceView = null;

    private Boolean touchedFlag = false;
    private Boolean isFocusing = false;

    private Vibrator vibrator;


    private String mFolderPath = "";
    private int mDateIndex;
    private int mPhotoIndex;
    private Bitmap mLayeredBitmap;





    //カメラサイズ
    private static final int PREVIEW_WIDTH = 640;
    private static final int PREVIEW_HEIGHT = 480;
    private static final int PICTURE_WIDTH = 640;
    private static final int PICTURE_HEIGHT = 480;


    public CameraView(Context context, String folderPath, int dateIndex, int photoIndex , Bitmap bmp) {
        super(context);

        myContext = context;
        mFolderPath = folderPath;
        mDateIndex = dateIndex;
        mPhotoIndex = photoIndex;
        mLayeredBitmap = bmp;



        vibrator = (Vibrator) myContext.getSystemService(Context.VIBRATOR_SERVICE);

        holder = getHolder();

        holder.addCallback(this);
        holder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);


    }



    @Override
    public void surfaceCreated(SurfaceHolder holder) {

        if( myCamera != null ){
            return;
        }

        myCamera = Camera.open();
        myCamera.setDisplayOrientation(90);




        try {
            myCamera.setPreviewDisplay(holder);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }




    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if(event.getAction() == MotionEvent.ACTION_DOWN) {

            if (myCamera != null && ! isFocusing ) {
                // 撮影実行(AF開始)
                myCamera.autoFocus(autoFocusListener_);
            }

        }
        return false;
    }

    // AF完了時のコールバック
    private Camera.AutoFocusCallback autoFocusListener_ = new Camera.AutoFocusCallback() {
        @Override
        public void onAutoFocus(boolean success, Camera camera) {
            isFocusing = true; // 処理中フラグ
            camera.autoFocus(null);
            //myCamera.takePicture(null, null, null);





            camera.takePicture(null, null, new Camera.PictureCallback() {
                @Override
                public void onPictureTaken(byte[] data, Camera camera) {
                    myTakePicture(data, camera);
                }
            });
        }
    };


    @Override
    public void onPictureTaken(byte[] data, Camera camera) {
        myTakePicture(data, camera);

    }

    private void myTakePicture(byte[] data, Camera camera) {


        String datName = getPhotoFileName();
        try {
            // データ保存
            savePhotoData(datName, data);
        } catch (Exception e) {
            if(camera != null) {
                camera.release();
                camera = null;
            }
        }



        // プレビュー再開
        if( touchedFlag == false ){
            vibrator.vibrate(100);
            touchedFlag = true;
        }
        camera.startPreview();
        touchedFlag = false;
        isFocusing = false;
    }

    private String getPhotoFileName(){
        String fileName = "";

        SimpleDateFormat date = new SimpleDateFormat("yyyyMMdd_kkmmss");
        fileName = mDateIndex + "_" + mPhotoIndex + "_" + date.format(new Date()) + ".jpg";

        return fileName;
    }





    private void savePhotoData(String datName, byte[] data) throws Exception {
        FileOutputStream outStream = null;

        try {


            // ローカルストレージ
            //InputStream in = myContext.openFileInput(datName);


            /*
            // 読み込む範囲
            //int previewWidth = myCamera.getParameters().getPreviewSize().width;
            //int previewHeight = myCamera.getParameters().getPreviewSize().height;
            int previewWidth = cameraWidth;
            int previewHeight =cameraHeight;
            // プレビューデータから Bitmap を生成
            Log.d("savePhotoData()", "112313212312132132132132132");
            //Bitmap bmp =  getBitmapImageFromYUV(data, previewWidth, previewHeight);
            Log.d("savePhotoData()", datName);
            //FileOutputStream out = myContext.openFileOutput(datName, Context.MODE_WORLD_READABLE);
            //bmp.compress(Bitmap.CompressFormat.JPEG, 100, out);
            //out.close();

            OutputStream out =myContext.openFileOutput(datName, Context.MODE_APPEND);
            out.write( data );
            out.close();
            */


            //////////////////////////////////////////////////////////
            // 回転
            int degrees = 0; //端末の向き(度換算)
            Matrix m = new Matrix(); //Bitmapの回転用Matrix
            m.setRotate(90-degrees);    // 向きが正しくなるように回転角度を補正
            Bitmap original = BitmapFactory.decodeByteArray(data, 0, data.length);
            Bitmap rotated = Bitmap.createBitmap( original, 0, 0, original.getWidth(), original.getHeight(), m, true);
            //////////////////////////////////////////////////////////



            // 合成
            Bitmap[] bitmapArray = new  Bitmap[2];
            bitmapArray[0] = rotated;
            bitmapArray[1] = mLayeredBitmap;
            Bitmap newBitmap = gousei(bitmapArray);




            ///*
            // SDカード
            String saveDir = mFolderPath;
            // SD カードフォルダを取得
            File file = new File(saveDir);
            // フォルダ作成
            if (!file.exists()) {
                if (!file.mkdir()) {
                    Log.e("Debug", "Make Dir Error");
                }
            }
            String imgPath = saveDir + "/" + datName;

            /*
            // data そのまま
            Log.d("savePhotoData()", datName);
            outStream = new FileOutputStream(imgPath);
            outStream.write(data);
            outStream.close();
            */

            FileOutputStream fos = null;
            try {
                fos = new FileOutputStream(imgPath);
                newBitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
                fos.close();
            } catch (Exception e) {
                e.printStackTrace();
            }

            original.recycle();
            rotated.recycle();
            kaihou(bitmapArray);



            // アンドロイドのデータベースへ登録
            // (登録しないとギャラリーなどにすぐに反映されないため)
            registAndroidDB(imgPath);
            //*/

        } catch (Exception e) {
            if(outStream != null) {
                outStream.close();
            }
            throw e;
        }
    }

    // 配列ゼロ板をキャンパスサイズにする。添え字が大きいほど上位レイヤー
    public Bitmap gousei(Bitmap[] bmp) {
        // ARGB_8888,RGB_565 ARGB_4444 RGB_565
        Bitmap rbmp = Bitmap.createBitmap(bmp[0].getWidth(),
                bmp[0].getHeight(), Bitmap.Config.RGB_565);
        Canvas canvas = new Canvas(rbmp);

        String wid1 = "" + bmp[0].getWidth();
        String hei1 = "" + bmp[0].getHeight();
        String wid2 = "" + bmp[1].getWidth();
        String hei2 = "" + bmp[1].getHeight();
        String widc = "" + canvas.getWidth();
        String heic = "" + canvas.getHeight();

        for (int i = 0; i < bmp.length; i++) {

            if( i != 0){
                Bitmap bmpRsz;
                bmpRsz = Bitmap.createScaledBitmap(bmp[i], bmp[0].getWidth(),bmp[0].getHeight(), false);

                canvas.drawBitmap(bmpRsz, 0, 0, (Paint) null);

            }else {

                canvas.drawBitmap(bmp[i], 0, 0, (Paint) null);
            }
        }

        return rbmp;
    }

    //任意のタイミングでメモリ解放を
    public void kaihou(Bitmap[] bmp){
        for (int i = 0; i < bmp.length; i++) {
            bmp[0].recycle();
        }
    }



    //  アンドロイドのデータベースへ画像のパスを登録
    private void registAndroidDB(String path) {
        // アンドロイドのデータベースへ登録
        // (登録しないとギャラリーなどにすぐに反映されないため)
        ContentValues values = new ContentValues();
        ContentResolver contentResolver = myContext.getContentResolver();
        values.put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg");
        values.put("_data", path);
        contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
    }





    public Bitmap getBitmapImageFromYUV(byte[] data, int width, int height) {
        YuvImage yuvimage = new YuvImage(data, ImageFormat.NV21, width, height, null);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        yuvimage.compressToJpeg(new Rect(0, 0, width, height), 80, baos);
        byte[] jdata = baos.toByteArray();
        BitmapFactory.Options bitmapFactoryOptions = new BitmapFactory.Options();
        bitmapFactoryOptions.inPreferredConfig = Bitmap.Config.RGB_565;
        Bitmap bmp = BitmapFactory.decodeByteArray(jdata, 0, jdata.length, bitmapFactoryOptions);
        return bmp;
    }








    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width,   int height) {

        Camera.Parameters parameters = myCamera.getParameters();


        //サイズ可変
        /*
        String previewsize = parameters.get("preview-size-values");
        String[] sizeArray = previewsize.split(",");
        String[] size = sizeArray[sizeArray.length/2].split("x");
        parameters.setPreviewSize( Integer.parseInt(size[0]), Integer.parseInt(size[1]) );

        String picturesize  = parameters.get("picture-size-values");
        sizeArray = picturesize.split(",");
        size = sizeArray[sizeArray.length/2].split("x");
        parameters.setPictureSize(Integer.parseInt(size[0]), Integer.parseInt(size[1]) );
        */


        parameters.setPreviewSize(PREVIEW_WIDTH, PREVIEW_HEIGHT);
        parameters.setPictureSize(PICTURE_WIDTH, PICTURE_HEIGHT);
        //parameters.setRotation(90);



        // 画面の大きさ
        // View内のView取得
        android.view.ViewGroup.LayoutParams lp = getLayoutParams();
        int ch = parameters.getPreviewSize().height;
        int cw = parameters.getPreviewSize().width;
        if(ch/cw > height/width){
            lp.width = width;
            lp.height = width*ch/cw;
        }else{
            lp.width = height*cw/ch;
            lp.height = height;
        }
        //surfaceView.setLayoutParams( lp );





        myCamera.setParameters(parameters);

        myCamera.startPreview();
    }


    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        myCamera.stopPreview();
        myCamera.release();
        myCamera = null;
    }

}
