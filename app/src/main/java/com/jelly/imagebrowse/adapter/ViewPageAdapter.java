package com.jelly.imagebrowse.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.support.annotation.Nullable;
import android.support.v4.view.PagerAdapter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.target.Target;
import com.bumptech.glide.request.transition.Transition;
import com.jelly.imagebrowse.R;
import com.jelly.imagebrowse.utils.WindowUtil;

import java.util.List;

import uk.co.senab.photoview.PhotoViewAttacher;


/**
 * 图片浏览ViewPageAdapter
 * Created by Jelly on 2016/3/10.
 */
public class ViewPageAdapter extends PagerAdapter {
    private Context context;
    private List<String> images;
    private PhotoViewAttacher.OnPhotoTapListener photoTabListener;
    private RequestOptions requestOption = new RequestOptions().fitCenter();

    public ViewPageAdapter(Context context, List<String> images, PhotoViewAttacher.OnPhotoTapListener photoTabListener) {
        this.context = context.getApplicationContext();
        this.images = images;
        this.photoTabListener = photoTabListener;
    }

    @Override
    public Object instantiateItem(final ViewGroup container, int position) {
        View view = LayoutInflater.from(context).inflate(R.layout.vp_item_image, container, false);
        view.setTag(new ViewHolder(context, view, images.get(position), photoTabListener, requestOption));
        container.addView(view);
        return view;
    }

    @Override
    public int getCount() {
        return images.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        View view = (View) object;
        Object viewHolder = view.getTag();
        if (viewHolder instanceof ViewHolder) {
            ((ViewHolder) viewHolder).destroy();
        }
        container.removeView(view);
    }

    private static class MyTarget extends SimpleTarget<Bitmap> {

        private PhotoViewAttacher viewAttacher;
        private Context context;

        public MyTarget(Context context, PhotoViewAttacher viewAttacher) {
            this.viewAttacher = viewAttacher;
            this.context = context;
        }

        @Override
        public void onResourceReady(Bitmap resource, Transition<? super Bitmap> transition) {
            int width = resource.getWidth();
            int height = resource.getHeight();

            int newWidth = width;
            int newHeight = height;

            int screenWidth = WindowUtil.getInstance().getScreenWidth(context);
            int screenHeight = WindowUtil.getInstance().getScreenHeight(context);

            if (width > screenWidth) {
                newWidth = screenWidth;
            }

            if (height > screenHeight) {
                newHeight = screenHeight;
            }


            if (newWidth == width && newHeight == height) {
                viewAttacher.getImageView().setImageBitmap(resource);
                viewAttacher.update();
                return;
            }

            //计算缩放比例
            float scaleWidth = ((float) newWidth) / width;
            float scaleHeight = ((float) newHeight) / height;

            Matrix matrix = new Matrix();
            matrix.postScale(scaleWidth, scaleHeight);

            Log.v("size", width + "");
            Log.v("size", height + "");

            Bitmap resizeBitmap = Bitmap.createBitmap(resource, 0, 0, width, height, matrix, true);

            viewAttacher.getImageView().setImageBitmap(resizeBitmap);
            viewAttacher.update();
        }
    }

    private static class ViewHolder {
        private final MyTarget viewTarget;
        private final PhotoViewAttacher photoViewAttacher;
        private final Context context;
        private final RequestListener<Bitmap> myLoadingListener;

        public ViewHolder(Context context, View view, String url, PhotoViewAttacher.OnPhotoTapListener photoTabListener, RequestOptions requestOption) {
            this.context = context;
            ImageView image = (ImageView) view.findViewById(R.id.image);
            View loadingView = view.findViewById(R.id.loading_bar);
            photoViewAttacher = new PhotoViewAttacher(image);
            viewTarget = new MyTarget(context, photoViewAttacher);
            myLoadingListener = new MyLoadingListener(loadingView);
            Glide.with(context)
                    .asBitmap()
                    .load(url)
                    .apply(requestOption)
                    .listener(myLoadingListener)
                    .into(viewTarget);
            if (photoTabListener != null) {
                photoViewAttacher.setOnPhotoTapListener(photoTabListener);
            }
        }

        public void destroy() {
            photoViewAttacher.cleanup();
            Glide.with(context).clear(viewTarget);
        }
    }

    private static class MyLoadingListener implements RequestListener<Bitmap> {
        View loadingView;

        public MyLoadingListener(View loadingView) {
            this.loadingView = loadingView;
        }

        @Override
        public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Bitmap> target, boolean isFirstResource) {
            if (loadingView != null) {
                loadingView.setVisibility(View.INVISIBLE);
            }
            return false;
        }

        @Override
        public boolean onResourceReady(Bitmap resource, Object model, Target<Bitmap> target, DataSource dataSource, boolean isFirstResource) {
            if (loadingView != null) {
                loadingView.setVisibility(View.INVISIBLE);
            }
            return false;
        }
    }
}
