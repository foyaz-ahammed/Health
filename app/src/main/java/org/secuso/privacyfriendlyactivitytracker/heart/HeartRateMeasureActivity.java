package org.secuso.privacyfriendlyactivitytracker.heart;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.hardware.Camera;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.TextureView;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.mikhaellopez.circularprogressbar.CircularProgressBar;

import org.secuso.privacyfriendlyactivitytracker.R;
import org.secuso.privacyfriendlyactivitytracker.Utils;
import org.secuso.privacyfriendlyactivitytracker.activities.ToolbarActivity;

import java.util.concurrent.CopyOnWriteArrayList;

/**
 * 심박수측정화면
 */
public class HeartRateMeasureActivity extends ToolbarActivity {
    CircularProgressBar circularProgressBar;
    TextureView graphTextureView;
    SurfaceView preview = null;
    TextView pulseValue;
    TextView measureStatus;
    TextView measureGuide;

    MeasureStore store;
    ChartDrawer chartDrawer;

    SurfaceHolder previewHolder = null;
    private static Camera camera = null;

    private final int REQUEST_CODE_CAMERA = 0;

    private static long startTime = 0;

    public int counter = 0;
    private int detectedValleys = 0;
    private final CopyOnWriteArrayList<Long> valleys = new CopyOnWriteArrayList<>();

    private final int measurementLength = 15000; // 측정길이
    private final int clipLength = 3500; // 처음에 측정에 들어가지 않는 시간

    private boolean isAlreadyCovered = false; // 이미 한번이상 카메라를 덮었는지 확인
    boolean isFirstDrawn = false; // 처음으로 그라프가 그려졌는지를 판별
    boolean isResultActivityStarted = false;

    public static final String MEASURE_RESULT = "measure_result";
    public static final String MEASURE_TIME = "measure_time";
    public static final String MEASURE_NOTE = "measure_note";
    public static final String MEASURE_STATUS = "measure_status";

    @SuppressLint("InvalidWakeLockTag")
    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.activity_heart_rate_measure);
        super.onCreate(savedInstanceState);

        graphTextureView = findViewById(R.id.graphTextureView);
        circularProgressBar = findViewById(R.id.circularProgressBar);
        pulseValue = findViewById(R.id.pulse_value);
        measureStatus = findViewById(R.id.measure_status);
        measureGuide = findViewById(R.id.measure_guide);
        preview = findViewById(R.id.preview);

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            preview.setVisibility(View.GONE);
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA},
                    REQUEST_CODE_CAMERA);
        } else {
            preview.setVisibility(View.VISIBLE);
        }

        chartDrawer = new ChartDrawer(graphTextureView, this);

        previewHolder = preview.getHolder();
        previewHolder.addCallback(surfaceCallback);
        previewHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CODE_CAMERA) {
            if (!(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                Toast.makeText(this, getString(R.string.cameraPermissionRequired), Toast.LENGTH_SHORT).show();
            } else {
                preview.setVisibility(View.VISIBLE);
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
            if (camera == null) {
                camera = Camera.open();
                camera.setDisplayOrientation(90);
            }
        }
    }

    @Override
    protected void onPause() {
        super.onPause();

        // App 이 background 상태로 넘어갔다 돌아올때 그라프표시를 위한 surfaceView 가 까만색으로 표시되는것을
        // 방지하기 위해 background 상태로 넘어갈때 그라프가 한번도 그려진적이 없으면 보임상태를 숨기기 진행
        if (!isFirstDrawn && graphTextureView.getVisibility() == View.VISIBLE)
            graphTextureView.setVisibility(View.GONE);

        if (camera != null) {
            // 카메라 해제
            camera.setPreviewCallback(null);
            camera.stopPreview();
            camera.release();
            camera = null;
        }
    }

    /**
     * 측정중인지 아닌지 현재 상태를 반영
     * @param isMeasuring 측정중이면 true 아니면 false
     */
    private void showStatus(boolean isMeasuring) {
        measureStatus.setVisibility(isMeasuring ? View.VISIBLE : View.GONE);
        measureGuide.setVisibility(View.VISIBLE);
        measureStatus.setText(getString(R.string.measuring));
        measureGuide.setText(isMeasuring ? getString(R.string.stay_measure) : getString(R.string.check_finger));
    }

    /**
     * 얻어진 값이 계곡 값인지 확인
     * @return 계곡 값이면 true 아니면 false
     */
    private boolean detectValley() {
        final int valleyDetectionWindowSize = 13;
        CopyOnWriteArrayList<Measurement<Integer>> subList = store.getLastStdValues(valleyDetectionWindowSize);
        if (subList.size() < valleyDetectionWindowSize) {
            return false;
        } else {
            Integer referenceValue = subList.get((int) Math.ceil(valleyDetectionWindowSize / 2f)).measurement;

            for (Measurement<Integer> measurement : subList) {
                if (measurement.measurement < referenceValue) return false;
            }

            // filter out consecutive measurements due to too high measurement rate
            return (!subList.get((int) Math.ceil(valleyDetectionWindowSize / 2f)).measurement.equals(
                    subList.get((int) Math.ceil(valleyDetectionWindowSize / 2f) - 1).measurement));
        }
    }

    private final Camera.PreviewCallback previewCallback = new Camera.PreviewCallback() {

        /**
         * {@inheritDoc}
         */
        @Override
        public void onPreviewFrame(byte[] data, Camera cam) {
            if (data == null) throw new NullPointerException();
            Camera.Size size = cam.getParameters().getPreviewSize();
            if (size == null) throw new NullPointerException();

            int width = size.width;
            int height = size.height;

            double redAvg;
            int redSum;
            redSum = Utils.decodeYUV420SPtoRedBlueGreenSum(data.clone(), width, height, 1);
            redAvg = (double) redSum / (width * height);

            // 카메라에 손가락을 덮었는지 확인
            if (redAvg < 200) {
                if (isAlreadyCovered)
                    showStatus(false);
                if (counter != 0)
                    circularProgressBar.setProgressWithAnimation(0, (long)50);
                counter = 0;
                store.clear();
                pulseValue.setText(getString(R.string.empty_value));
                detectedValleys = 0;
                valleys.clear();
            } else {
                isAlreadyCovered = true;
                if (counter == 0) {
                    circularProgressBar.setProgressWithAnimation(100, (long)15000);
                    startTime = System.currentTimeMillis();
                    showStatus(true);
                }
                counter++;

                if (System.currentTimeMillis() - startTime >= clipLength) {
                    store.add(redSum);

                    if (detectValley()) {
                        detectedValleys++;
                        valleys.add(store.getLastTimestamp().getTime());

                        int currentValue = (int) ((valleys.size() == 1)
                                ? (60f * (detectedValleys) / (Math.max(1, ((System.currentTimeMillis() - startTime) - clipLength) / 1000f)))
                                : (60f * (detectedValleys - 1) / (Math.max(1, (valleys.get(valleys.size() - 1) - valleys.get(0)) / 1000f))));
                        pulseValue.setText(String.valueOf(currentValue));
                    }

                    // 그라프표시부분의 보임상태가 GONE 이면 VISIBLE 로 변경
                    if (graphTextureView.getVisibility() == View.GONE)
                        graphTextureView.setVisibility(View.VISIBLE);

                    chartDrawer.draw(store.getStdValues());

                    if (!isFirstDrawn)
                        isFirstDrawn = true;

                    if (System.currentTimeMillis() - startTime >= measurementLength) {

                        measureGuide.setVisibility(View.GONE);
                        measureStatus.setText(getString(R.string.finished));
                        finish();

                        if (!isResultActivityStarted) {
                            Intent intent = new Intent(getApplicationContext(), HeartRateResultActivity.class);
                            intent.putExtra(MEASURE_RESULT, Integer.parseInt(pulseValue.getText().toString()));
                            intent.putExtra(MEASURE_TIME, System.currentTimeMillis());
                            startActivity(intent);
                            isResultActivityStarted = true;
                        }
                    }
                }
            }
        }
    };

    private final SurfaceHolder.Callback surfaceCallback = new SurfaceHolder.Callback() {

        @Override
        public void surfaceCreated(SurfaceHolder holder) {
            try {
                if (camera != null) {
                    store = new MeasureStore();
                    camera.setPreviewDisplay(previewHolder);
                    camera.setPreviewCallback(previewCallback);
                }
            } catch (Throwable t) {
                Log.e("surfaceCallback", "Exception in setPreviewDisplay()", t);
            }
        }

        @Override
        public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
            if (previewHolder.getSurface() == null) {
                return;
            }

            try {
                camera.stopPreview();
            } catch (Exception e) {
                // ignore: tried to stop a non-existent preview
            }

            if (camera != null) {
                Camera.Parameters parameters = camera.getParameters();
                parameters.setFlashMode(Camera.Parameters.FLASH_MODE_TORCH);

                Camera.Size size = getSmallestPreviewSize(width, height, parameters);
                if (size != null) {
                    parameters.setPreviewSize(size.width, size.height);
                }

                camera.setParameters(parameters);
                camera.startPreview();
            }
        }


        @Override
        public void surfaceDestroyed(SurfaceHolder holder) {
        }
    };

    /**
     * 가장 작은 preview 카메라크기를 얻는 함수
     * @param width preview 너비
     * @param height preview 높이
     * @param parameters
     * @return
     */
    private static Camera.Size getSmallestPreviewSize(int width, int height, Camera.Parameters parameters) {
        Camera.Size result = null;
        for (Camera.Size size : parameters.getSupportedPreviewSizes()) {
            if (size.width <= width && size.height <= height) {
                if (result == null) {
                    result = size;
                } else {
                    int resultArea = result.width * result.height;
                    int newArea = size.width * size.height;
                    if (newArea < resultArea) result = size;
                }
            }
        }
        return result;
    }
}