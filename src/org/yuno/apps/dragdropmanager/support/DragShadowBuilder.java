package org.yuno.apps.dragdropmanager.support;

import java.lang.ref.WeakReference;

import org.yuno.apps.dragdropmanager.BuildConfig;
import org.yuno.apps.dragdropmanager.DragDropManager;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Point;
import android.graphics.drawable.BitmapDrawable;
import android.os.Build;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

public class DragShadowBuilder {
	private final WeakReference<View> mView;

	// EVENT

	public DragShadowBuilder() {
		mView = new WeakReference<View>(null);
	}

	public DragShadowBuilder(View view) {
		mView = new WeakReference<View>(view);
	}

	@SuppressLint("NewApi")
	public DragShadowBuilder(
			android.view.View.DragShadowBuilder dragShadowBuilder) {
		mView = new WeakReference<View>(dragShadowBuilder.getView());
	}

	// PUBLIC

	public void onProvideShadowMetrics(Point shadowSize, Point shadowTouchPoint) {
		final View view = mView.get();

		if (view != null) {
			shadowSize.set(view.getWidth(), view.getHeight());
			shadowTouchPoint.set(shadowSize.x / 2, shadowSize.y / 2);
		} else {
			if (BuildConfig.DEBUG) {
				Log.e(DragDropManager.TAG,
						"Asked for drag thumb metrics but no view");
			}
		}
	}

	public void onDrawShadow(Canvas canvas) {
		final View view = mView.get();
		if (view != null) {
			view.draw(canvas);
		} else {
			Log.e(DragDropManager.TAG, "Asked to draw drag shadow but no view");
		}
	}

	@SuppressWarnings("deprecation")
	@SuppressLint("NewApi")
	public View supportCreateShadowView(Context context) {
		// TODO: use onDrawShadow instead to draw bitmap in the provided canvas

		final View view = mView.get();
		final ImageView dragView = new ImageView(context);

		// TODO: use onSupportProvideShadowMetrics

		dragView.measure(view.getWidth(), view.getHeight());

		Bitmap returnedBitmap = Bitmap.createBitmap(view.getWidth(),
				view.getHeight(), Bitmap.Config.ARGB_8888);
		Canvas canvas = new Canvas(returnedBitmap);
		view.draw(canvas);

		if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN) {
			dragView.setBackgroundDrawable(new BitmapDrawable(view
					.getResources(), returnedBitmap));
		} else {
			dragView.setBackground(new BitmapDrawable(view.getResources(),
					returnedBitmap));
		}

		// TODO: return invisible view for empty constructor

		return dragView;
	}

	// GET

	public View getView() {
		return mView.get();
	}

	// STATIC

	@SuppressLint("NewApi")
	public static android.view.View.DragShadowBuilder supportConvert(
			DragShadowBuilder shadowBuilder) {
		return new android.view.View.DragShadowBuilder(shadowBuilder.getView());
	}
}