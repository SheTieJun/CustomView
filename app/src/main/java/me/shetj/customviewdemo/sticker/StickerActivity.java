package me.shetj.customviewdemo.sticker;

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.Layout;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.io.File;
import java.util.Arrays;
import java.util.Objects;

import me.shetj.customviewdemo.R;

public class StickerActivity extends AppCompatActivity {
  private static final String TAG = StickerActivity.class.getSimpleName();
  public static final int PERM_RQST_CODE = 110;
  private StickerView stickerView;
  private TextSticker sticker;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_sticker);

    stickerView = (StickerView) findViewById(R.id.sticker_view);
    Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);

    //currently you can config your own icons and icon event
    //the event you can custom
    BitmapStickerIcon deleteIcon = new BitmapStickerIcon(ContextCompat.getDrawable(this,
            R.drawable.sticker_ic_close_white_18dp),
            BitmapStickerIcon.LEFT_TOP);
    deleteIcon.setIconEvent(new DeleteIconEvent());

    BitmapStickerIcon zoomIcon = new BitmapStickerIcon(ContextCompat.getDrawable(this,
            R.drawable.sticker_ic_scale_white_18dp),
            BitmapStickerIcon.RIGHT_BOTOM);
    zoomIcon.setIconEvent(new ZoomIconEvent());

    BitmapStickerIcon flipIcon = new BitmapStickerIcon(ContextCompat.getDrawable(this,
             R.drawable.sticker_ic_flip_white_18dp),
            BitmapStickerIcon.RIGHT_TOP);
    flipIcon.setIconEvent(new FlipHorizontallyEvent());

    BitmapStickerIcon heartIcon =
            new BitmapStickerIcon(ContextCompat.getDrawable(this, R.drawable.ic_favorite_white_24dp),
                    BitmapStickerIcon.LEFT_BOTTOM);
    heartIcon.setIconEvent(new HelloIconEvent());

    stickerView.setIcons(Arrays.asList(deleteIcon, zoomIcon, flipIcon, heartIcon));

    //default icon layout
    //stickerView.configDefaultIcons();

    stickerView.setBackgroundColor(Color.WHITE);
    stickerView.setLocked(false);
    stickerView.setConstrained(true);

    sticker = new TextSticker(this);

    sticker.setDrawable(Objects.requireNonNull(ContextCompat.getDrawable(getApplicationContext(),
            R.drawable.sticker_transparent_background)));
    sticker.setText("Hello, world!\nHello, world!");
    sticker.setTextColor(Color.BLACK);
    sticker.setTextAlign(Layout.Alignment.ALIGN_CENTER);
    sticker.resizeText();

    stickerView.setOnStickerOperationListener(new StickerView.OnStickerOperationListener() {
      @Override
      public void onStickerAdded(@NonNull Sticker sticker) {
        Log.d(TAG, "onStickerAdded");
      }

      @Override
      public void onStickerClicked(@NonNull Sticker sticker) {
        //stickerView.removeAllSticker();
        if (sticker instanceof TextSticker) {
          ((TextSticker) sticker).setTextColor(Color.RED);
          stickerView.replace(sticker);
          stickerView.invalidate();
        }
        Log.d(TAG, "onStickerClicked");
      }

      @Override
      public void onStickerDeleted(@NonNull Sticker sticker) {
        Log.d(TAG, "onStickerDeleted");
      }

      @Override
      public void onStickerDragFinished(@NonNull Sticker sticker) {
        Log.d(TAG, "onStickerDragFinished");
      }

      @Override
      public void onStickerTouchedDown(@NonNull Sticker sticker) {
        Log.d(TAG, "onStickerTouchedDown");
      }

      @Override
      public void onStickerZoomFinished(@NonNull Sticker sticker) {
        Log.d(TAG, "onStickerZoomFinished");
      }

      @Override
      public void onStickerFlipped(@NonNull Sticker sticker) {
        Log.d(TAG, "onStickerFlipped");
      }

      @Override
      public void onStickerDoubleTapped(@NonNull Sticker sticker) {
        Log.d(TAG, "onDoubleTapped: double tap will be with two click");
      }
    });

    if (toolbar != null) {
      toolbar.setTitle(R.string.app_name);
      toolbar.inflateMenu(R.menu.menu_save);
      toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
        @Override
        public boolean onMenuItemClick(MenuItem item) {

          return false;
        }
      });
    }

    if (ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
            != PackageManager.PERMISSION_GRANTED
            || ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
            != PackageManager.PERMISSION_GRANTED) {
      ActivityCompat.requestPermissions(this,
              new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, PERM_RQST_CODE);
    } else {
      loadSticker();
    }
  }

  private void loadSticker() {
    Drawable drawable =
            ContextCompat.getDrawable(this, R.drawable.exo_icon_fullscreen_enter);
    Drawable drawable1 =
            ContextCompat.getDrawable(this, R.drawable.exo_icon_fullscreen_enter);
    stickerView.addSticker(new DrawableSticker(drawable));
    stickerView.addSticker(new DrawableSticker(drawable1), Sticker.Position.BOTTOM | Sticker.Position.RIGHT);

    Drawable bubble = ContextCompat.getDrawable(this, R.mipmap.shetj_logo);
    stickerView.addSticker(
            new TextSticker(getApplicationContext())
                    .setDrawable(bubble)
                    .setText("Sticker\n")
                    .setMaxTextSize(14)
                    .resizeText()
            , Sticker.Position.TOP);
  }

  @Override
  public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                         @NonNull int[] grantResults) {
    super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    if (requestCode == PERM_RQST_CODE && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
      loadSticker();
    }
  }

  public void testReplace(View view) {
    if (stickerView.replace(sticker)) {
      Toast.makeText(StickerActivity.this, "Replace Sticker successfully!", Toast.LENGTH_SHORT).show();
    } else {
      Toast.makeText(StickerActivity.this, "Replace Sticker failed!", Toast.LENGTH_SHORT).show();
    }
  }

  public void testLock(View view) {
    stickerView.setLocked(!stickerView.isLocked());
  }

  public void testRemove(View view) {
    if (stickerView.removeCurrentSticker()) {
      Toast.makeText(StickerActivity.this, "Remove current Sticker successfully!", Toast.LENGTH_SHORT)
              .show();
    } else {
      Toast.makeText(StickerActivity.this, "Remove current Sticker failed!", Toast.LENGTH_SHORT)
              .show();
    }
  }

  public void testRemoveAll(View view) {
    stickerView.removeAllStickers();
  }

  public void reset(View view) {
    stickerView.removeAllStickers();
    loadSticker();
  }

  public void testAdd(View view) {
    final TextSticker sticker = new TextSticker(this);
    sticker.setText("Hello, world!");
    sticker.setTextColor(Color.BLUE);
    sticker.setTextAlign(Layout.Alignment.ALIGN_CENTER);
    sticker.resizeText();

    stickerView.addSticker(sticker);
  }
}
