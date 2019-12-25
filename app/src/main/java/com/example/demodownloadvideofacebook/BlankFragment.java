package com.example.demodownloadvideofacebook;


import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.liulishuo.filedownloader.BaseDownloadTask;
import com.liulishuo.filedownloader.FileDownloadSampleListener;
import com.liulishuo.filedownloader.FileDownloader;
import com.liulishuo.filedownloader.util.FileDownloadUtils;

import java.io.File;


/**
 * A simple {@link Fragment} subclass.
 */
public class BlankFragment extends Fragment {

    Button startBtn4, pauseBtn4, deleteBtn4;
    TextView detailTv4, speedTv4;
    ProgressBar progressBar4;
    private int downloadId4;
    private String chunkedFilePath;
    public BlankFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_blank, container, false);
        assignViews(view);
        chunkedFilePath = FileDownloadUtils.getDefaultSaveRootPath() + File.separator + ".mp4";
        initChunkTransferEncodingDataAction();
        return view;
    }
    private void initChunkTransferEncodingDataAction() {
        // task 4
        startBtn4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                downloadId4 = createDownloadTask().start();
            }
        });

        pauseBtn4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FileDownloader.getImpl().pause(downloadId4);
            }
        });

        deleteBtn4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new File(chunkedFilePath).delete();
                new File(FileDownloadUtils.getTempPath(chunkedFilePath)).delete();
            }
        });
    }
    private void assignViews(View view) {
        startBtn4 = view.findViewById(R.id.start_btn_4);
        pauseBtn4 = view.findViewById(R.id.pause_btn_4);
        deleteBtn4 = view.findViewById(R.id.delete_btn_4);
        detailTv4 = view.findViewById(R.id.detail_tv_4);
        speedTv4 = view.findViewById(R.id.speed_tv_4);
        progressBar4 = view.findViewById(R.id.progressBar_4);
    }
    private BaseDownloadTask createDownloadTask() {
        final ViewHolder tag;
        final String url;
        boolean isDir = false;
        String path;

        url = "https://video.xx.fbcdn.net/v/t42.9040-2/81499803_991843541192210_5938973591704961024_n.mp4?_nc_cat=106&efg=eyJ2ZW5jb2RlX3RhZyI6InN2ZV9zZCJ9&_nc_ohc=R2VV3grn59QAQlSWtxiY86QElQ8P_mGPntxPWTd3mUCt6LlHmPwXaUpQw&_nc_ht=video.fhan2-2.fna&oh=ca93d359aa5063c2eb136087c2110801&oe=5E03A12B";
        tag = new ViewHolder(progressBar4, detailTv4, speedTv4);
        path = chunkedFilePath;

        return FileDownloader.getImpl().create(url)
                .setPath(path, isDir)
                .setCallbackProgressTimes(300)
                .setMinIntervalUpdateSpeed(400)
                .setTag(tag)
                .setListener(new FileDownloadSampleListener() {

                    @Override
                    protected void pending(BaseDownloadTask task, int soFarBytes, int totalBytes) {
                        super.pending(task, soFarBytes, totalBytes);
                        ((ViewHolder) task.getTag()).updatePending(task);
                    }

                    @Override
                    protected void progress(BaseDownloadTask task, int soFarBytes, int totalBytes) {
                        super.progress(task, soFarBytes, totalBytes);
                        ((ViewHolder) task.getTag()).updateProgress(soFarBytes, totalBytes,
                                task.getSpeed());
                    }

                    @Override
                    protected void error(BaseDownloadTask task, Throwable e) {
                        super.error(task, e);
                        ((ViewHolder) task.getTag()).updateError(e, task.getSpeed());
                    }

                    @Override
                    protected void connected(BaseDownloadTask task, String etag, boolean isContinue, int soFarBytes, int totalBytes) {
                        super.connected(task, etag, isContinue, soFarBytes, totalBytes);
                        ((ViewHolder) task.getTag()).updateConnected(etag, task.getFilename());
                    }

                    @Override
                    protected void paused(BaseDownloadTask task, int soFarBytes, int totalBytes) {
                        super.paused(task, soFarBytes, totalBytes);
                        ((ViewHolder) task.getTag()).updatePaused(task.getSpeed());
                    }

                    @Override
                    protected void completed(BaseDownloadTask task) {
                        super.completed(task);
                        ((ViewHolder) task.getTag()).updateCompleted(task);
                    }

                    @Override
                    protected void warn(BaseDownloadTask task) {
                        super.warn(task);
                        ((ViewHolder) task.getTag()).updateWarn();
                    }
                });
    }

    private static class ViewHolder {
        private ProgressBar pb;
        private TextView detailTv;
        private TextView speedTv;
        private TextView filenameTv;

        public ViewHolder(final ProgressBar pb, final TextView detailTv, final TextView speedTv) {
            this.pb = pb;
            this.detailTv = detailTv;
            this.speedTv = speedTv;
        }

        public void setFilenameTv(TextView filenameTv) {
            this.filenameTv = filenameTv;
        }

        private void updateSpeed(int speed) {
            speedTv.setText(String.format("%dKB/s", speed));
        }

        public void updateProgress(final int sofar, final int total, final int speed) {
            if (total == -1) {
                // chunked transfer encoding data
                pb.setIndeterminate(true);
            } else {
                pb.setMax(total);
                pb.setProgress(sofar);
            }

            updateSpeed(speed);

            if (detailTv != null) {
                detailTv.setText(String.format("sofar: %d total: %d", sofar, total));
            }
        }

        public void updatePending(BaseDownloadTask task) {
            if (filenameTv != null) {
                filenameTv.setText(task.getFilename());
            }
        }

        public void updatePaused(final int speed) {
            updateSpeed(speed);
            pb.setIndeterminate(false);
        }

        public void updateConnected(String etag, String filename) {
            if (filenameTv != null) {
                filenameTv.setText(filename);
            }
        }

        public void updateWarn() {
            pb.setIndeterminate(false);
        }

        public void updateError(final Throwable ex, final int speed) {
            updateSpeed(speed);
            pb.setIndeterminate(false);
            ex.printStackTrace();
        }

        public void updateCompleted(final BaseDownloadTask task) {


            if (detailTv != null) {
                detailTv.setText(String.format("sofar: %d total: %d",
                        task.getSmallFileSoFarBytes(), task.getSmallFileTotalBytes()));
            }

            updateSpeed(task.getSpeed());
            pb.setIndeterminate(false);
            pb.setMax(task.getSmallFileTotalBytes());
            pb.setProgress(task.getSmallFileSoFarBytes());
        }
    }
}
