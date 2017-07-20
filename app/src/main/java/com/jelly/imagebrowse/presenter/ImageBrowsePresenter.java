package com.jelly.imagebrowse.presenter;

import android.content.Intent;

import com.jelly.imagebrowse.view.ImageBrowseView;

import java.util.List;

/**
 * Created by Jelly on 2016/9/3.
 */
public class ImageBrowsePresenter {

    private ImageBrowseView view;
    private List<String> images;
    private int position;
    private String[] imageTypes = new String[] { ".jpg",".png", ".jpeg","webp"};

    public ImageBrowsePresenter(ImageBrowseView view) {
        this.view = view;
    }

    public void loadImage(){
        Intent intent = view.getDataIntent();
        images = MainPresenter.getTestImage();
        position = intent.getIntExtra("position",0);
        view.setImageBrowse(images,position);
    }

    public List<String> getImages() {
        return images;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public String getImageType(String imageUrl){
        String imageType = "";
        if(imageUrl.endsWith(imageTypes[0])){
            imageType = "jpg";
        }else if(imageUrl.endsWith(imageTypes[1])){
            imageType = "png";
        }else{
            imageType = "jpeg";
        }
        return imageType;
    }

}

