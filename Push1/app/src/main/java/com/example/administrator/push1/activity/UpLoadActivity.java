package com.example.administrator.push1.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import com.example.administrator.push1.R;
import com.example.administrator.push1.bean.UploadItem;
import com.example.administrator.push1.bean.UserInfo;
import com.example.administrator.push1.util.DbUtil;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by Administrator on 2017/7/11.
 */

public class UpLoadActivity extends Activity {

    private GridView gridView1;                 //网格显示缩略图
    private final int IMAGE_OPEN = 1;      //打开图片标记
    private final int GET_DATA = 2;           //获取处理后图片标记
    private final int TAKE_PHOTO = 3;       //拍照标记
    private String pathImage;                     //选择图片路径
    private Bitmap bmp;                             //导入临时图片
    private Uri imageUri;                            //拍照Uri
    private String pathTakePhoto;              //拍照路径
    private Button uploadButton;              //点击上传按钮
    private EditText detailEdit;
    private Handler mImageLoadHandler;

    //存储Bmp图像
    private ArrayList<HashMap<String, Object>> imageItem;
    //适配器
    private SimpleAdapter simpleAdapter;
    private int takePhotoIndex = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.upload);
        gridView1 = findViewById(R.id.gridView1);
        uploadButton = findViewById(R.id.button1);
        detailEdit = findViewById(R.id.editText1);
        mImageLoadHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                switch (msg.what) {
                    case 1:
                        HashMap<String, Object> map = new HashMap<String, Object>();
                        map.put("itemImage", bmp);
                        map.put("pathImage", (String)msg.obj);
                        map.put("fromCamera", msg.arg1);
                        imageItem.add(map);
                        simpleAdapter.notifyDataSetChanged();
                        break;
                }
            }
        };
        /*
         * 载入默认图片添加图片加号
         * 通过适配器实现
         * SimpleAdapter参数imageItem为数据源 R.layout.griditem_addpic为布局
         */
        bmp = BitmapFactory.decodeResource(getResources(), R.drawable.gridview_addpic); //加号
        imageItem = new ArrayList<HashMap<String, Object>>();
        HashMap<String, Object> map = new HashMap<String, Object>();
        map.put("itemImage", bmp);
        map.put("pathImage", "add_pic");
        imageItem.add(map);
        simpleAdapter = new SimpleAdapter(this,
                imageItem, R.layout.griditem_addpic,
                new String[] { "itemImage"}, new int[] { R.id.imageView1});
        /*
         * HashMap载入bmp图片在GridView中不显示,但是如果载入资源ID能显示 如
         * map.put("itemImage", R.drawable.img);
         * 解决方法:
         *              1.自定义继承BaseAdapter实现
         *              2.ViewBinder()接口实现
         *  参考 http://blog.csdn.net/admin_/article/details/7257901
         */
        simpleAdapter.setViewBinder(new SimpleAdapter.ViewBinder() {
            @Override
            public boolean setViewValue(View view, Object data,
                                        String textRepresentation) {
                // TODO Auto-generated method stub
                if(view instanceof ImageView && data instanceof Bitmap){
                    ImageView i = (ImageView)view;
                    i.setImageBitmap((Bitmap) data);
                    return true;
                }
                return false;
            }
        });
        gridView1.setAdapter(simpleAdapter);
        gridView1.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View v, int position, long id)
            {
                if( imageItem.size() == 10) { //第一张为默认图片
                    Toast.makeText(UpLoadActivity.this, "图片数9张已满", Toast.LENGTH_SHORT).show();
                }
                else if(position == 0) { //点击图片位置为+ 0对应0张图片
                    //Toast.makeText(MainActivity.this, "添加图片", Toast.LENGTH_SHORT).show();
                    AddImageDialog();
                }
                else {
                    DeleteDialog(position);
                    //Toast.makeText(MainActivity.this, "点击第" + (position + 1) + " 号图片",
                    //		Toast.LENGTH_SHORT).show();
                }

            }
        });
        uploadButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                UploadItem item = new UploadItem();
                UserInfo userInfo = DbUtil.getLocalUser();
                item.setUserid(userInfo.getAccount());
                item.setPassword(userInfo.getPassword());
                item.setDetail(detailEdit.getText().toString());
                ArrayList<String> files = new ArrayList<String>();
                for (int i = 0; i < imageItem.size(); i++) {
                    HashMap map = imageItem.get(i);
                    String path = (String) map.get("pathImage");
                    if (!"add_pic".equals(path)) {
                        files.add(path);
                        Log.d("TEST", "upload :" + path);
                    }

                }
                item.setFiles(files);
            }
        });

    }
    //获取图片路径 响应startActivityForResult
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        for (int i = 0; i < imageItem.size(); i++) {
            HashMap map = imageItem.get(i);
            Log.d("TEST", "onActivityResult ");
        }
        //打开图片
        if (resultCode == RESULT_OK && requestCode == IMAGE_OPEN) {
            Uri uri = data.getData();
            if (!TextUtils.isEmpty(uri.getAuthority())) {
                //查询选择图片
                Cursor cursor = getContentResolver().query(
                        uri,
                        new String[]{MediaStore.Images.Media.DATA},
                        null,
                        null,
                        null);
                //返回 没找到选择图片
                if (null == cursor) {
                    Log.d("TEST", "path null");
                    return;
                }
                //光标移动至开头 获取图片路径
                cursor.moveToFirst();
                String path = cursor.getString(cursor
                        .getColumnIndex(MediaStore.Images.Media.DATA));
                Log.d("TEST", "path :" + path);
                new Thread(new LoadImgRunnable(path, false)).start();
            }
        } else if(resultCode==RESULT_OK && requestCode==TAKE_PHOTO) {
            Log.d("TEST", "onActivityResult TAKE_PHOTO");
            if (imageUri.getPath() != null) {
                new Thread(new LoadImgRunnable(imageUri.getPath(), true)).start();
            }
        }
    }
    /*
     * 添加图片 可通过本地添加、拍照添加
     */
    protected void AddImageDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(UpLoadActivity.this);
        builder.setTitle("添加图片");
        builder.setIcon(R.drawable.ic_launcher);
        builder.setCancelable(false); //不响应back按钮
        builder.setItems(new String[] {"本地相册选择","手机相机添加","取消选择图片"},
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // TODO Auto-generated method stub
                        switch(which) {
                            case 0: //本地相册
                                dialog.dismiss();
                                Intent intent = new Intent(Intent.ACTION_PICK,
                                        android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                                startActivityForResult(intent, IMAGE_OPEN);
                                //通过onResume()刷新数据
                                break;
                            case 1: //手机相机
                                dialog.dismiss();
                                File outputImage = new File(Environment.getExternalStorageDirectory().getPath(), "push" + takePhotoIndex + ".jpg");
                                takePhotoIndex++;
                                pathTakePhoto = outputImage.toString();
                                try {
                                    if(outputImage.exists()) {
                                        outputImage.delete();
                                    }
                                    outputImage.createNewFile();
                                } catch(Exception e) {
                                    e.printStackTrace();
                                }
                                imageUri = Uri.fromFile(outputImage);
                                Intent intentPhoto = new Intent("android.media.action.IMAGE_CAPTURE"); //拍照
                                intentPhoto.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
                                startActivityForResult(intentPhoto, TAKE_PHOTO);
                                break;
                            case 2: //取消添加
                                dialog.dismiss();
                                break;
                            default:
                                break;
                        }
                    }
                });
        //显示对话框
        builder.create().show();
    }

    /*
     * Dialog对话框提示用户删除操作
     * position为删除图片位置
     */
    protected void DeleteDialog(final int position) {
        AlertDialog.Builder builder = new AlertDialog.Builder(UpLoadActivity.this);
        builder.setMessage("确认移除已添加图片吗？");
        builder.setTitle("提示");
        builder.setPositiveButton("确认", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                HashMap map = imageItem.remove(position);
                String path = (String) map.get("pathImage");
                Log.d("TEST", "DELETE FILE 0:" + Environment.getExternalStorageDirectory().getPath() + "/push");
                int fromCamera = (int) map.get("fromCamera");
                if (fromCamera == 1) {
                    Log.d("TEST", "DELETE FILE 1:" + path);
                    File imgfile = new File(path);
                    if (imgfile.exists()) {
                        Log.d("TEST", "DELETE FILE:" + path);
                        imgfile.delete();
                    }
                }
                simpleAdapter.notifyDataSetChanged();
            }
        });
        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.create().show();
    }

    class LoadImgRunnable implements Runnable {

        private String imgPath;
        private boolean fromCamera;

        public LoadImgRunnable(String path, boolean fromCamera) {
            imgPath = path;
            this.fromCamera = fromCamera;
        }

        @Override
        public void run() {

            try {
                Log.d("TEST", "start thread :" + imgPath);
                FileInputStream inputStream = new FileInputStream(imgPath);
                bmp = BitmapFactory.decodeStream(inputStream);
                Message message = new Message();
                message.what = 1;
                if (fromCamera) {
                    message.arg1 = 1;
                } else {
                    message.arg1 = 0;
                }
                message.obj = imgPath;
                mImageLoadHandler.sendMessage(message);
                inputStream.close();
            } catch (FileNotFoundException e) {
                Log.d("TEST", "FileNotFoundException:" + imgPath);
                e.printStackTrace();
            } catch (IOException e) {
                Log.d("TEST", "IOException:" + imgPath);
                e.printStackTrace();
            }
        }
    }

    class UploadRunnable implements Runnable {

        private UploadItem mUploadItem;

        public UploadRunnable(UploadItem item) {
            mUploadItem = item;
        }

        @Override
        public void run() {

        }
    }


    @Override
    protected void onDestroy() {
        for (int i = 0; i < imageItem.size(); i++) {
            HashMap map = imageItem.get(i);
            Log.d("TEST", "DELETE FILE onDestory");
            String path = (String) map.get("pathImage");
            int fromCamera = (int) map.get("fromCamera");
            if (fromCamera == 1) {
                Log.d("TEST", "DELETE FILE 1:" + path);
                File imgfile = new File(path);
                if (imgfile.exists()) {
                    Log.d("TEST", "DELETE FILE:" + path);
                    imgfile.delete();
                }
            }
        }
        imageItem.clear();
        super.onDestroy();
    }
}
