package edu.mbryla.andlogger.preview;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import android.app.ListActivity;
import android.os.Bundle;
import android.os.Handler;
import android.widget.ArrayAdapter;
import edu.mbryla.andlogger.database.dao.AccelerationLogDAO;
import edu.mbryla.andlogger.database.dao.LocationLogDAO;
import edu.mbryla.andlogger.database.models.DbLog;
import edu.mbryla.andlogger.settings.AppProperties;


public class PreviewActivity extends ListActivity {
    private LocationLogDAO locationDao;
    private AccelerationLogDAO accelerationDao;

    private ArrayAdapter<DbLog> adapter;
    private Handler handler = new Handler();

    private Runnable updater = new Runnable() {

        @Override
        public void run() {
            adapter.clear();
            adapter.addAll(getLogs());

            handler.postDelayed(updater, (Long) AppProperties.previewProperties
                    .get("preview-rate-ms"));
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        locationDao = new LocationLogDAO(this);
        accelerationDao = new AccelerationLogDAO(this);

        adapter = new ArrayAdapter<DbLog>(this,
                android.R.layout.simple_list_item_1, getLogs());
        setListAdapter(adapter);

        handler.postDelayed(updater,
                (Long) AppProperties.previewProperties.get("preview-rate-ms"));
    }

    @Override
    protected void onDestroy() {
        handler.removeCallbacks(updater);
        super.onDestroy();
    }

    private List<DbLog> getLogs() {
        List<DbLog> ret = new ArrayList<DbLog>();

        ret.addAll(locationDao.loadAll());
        ret.addAll(accelerationDao.loadAll());

        Collections.sort(ret, new Comparator<DbLog>() {

            @Override
            public int compare(DbLog lhs, DbLog rhs) {
                return -lhs.getTimestamp().compareTo(rhs.getTimestamp());
            }
        });

        ret = ret.subList(0, (Integer) AppProperties.previewProperties
                .get("preview-logs-limit"));

        return ret;
    };
}
