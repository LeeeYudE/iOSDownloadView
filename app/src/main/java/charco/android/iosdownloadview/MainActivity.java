package charco.android.iosdownloadview;

import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.SeekBar;
import android.widget.Toast;

import charco.android.iosdownloadview.view.DownloadView;

public class MainActivity extends AppCompatActivity {

    private DownloadView mDownloadView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
    }

    private void initView() {
        mDownloadView = findViewById(R.id.download);
        mDownloadView.setDownloadLietener(new DownloadView.OnDownloadFinishListener() {
            @Override
            public void onDownloadFinish() {
                Toast.makeText(getApplicationContext(),"下载完成",Toast.LENGTH_SHORT).show();
            }
        });
        SeekBar seekBar = findViewById(R.id.seekbar);
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                mDownloadView.setPercent(i);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
    }


    public void stop(View view) {
        mDownloadView.stop();
    }


    public void restart(View view) {
        mDownloadView.restart();
    }

    public void reset(View view) {
        mDownloadView.reset();
    }
}
