package com.ctftek.controlmanager;

import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Toast;

import com.afa.tourism.greendao.gen.DaoMaster;
import com.afa.tourism.greendao.gen.DaoSession;
import com.afa.tourism.greendao.gen.ReservePlanDao;
import com.ctftek.controlmanager.model.AdapterModel;
import com.ctftek.controlmanager.model.MyButton;

import java.util.List;

public class MainActivity extends AppCompatActivity implements AdapterView.OnItemClickListener, View.OnClickListener {

    public static final String TAG = "MainActivity";
    private GridView gridView;
    private RectGridViewAdapter adapter;
    private AdapterModel adapterModel;
    private EditText ipAddress, port;
    private Button saveIpPort;
    private Button saveName;
    private Spinner spinnerRow;
    private Spinner spinnerColumn;
    private Spinner spinnerPlan;
    private EditText planName;
    private MyButton runPlan;
    private EditText command1, command2, command3, command4, command5;
    private Button sendCommand1, sendCommand2, sendCommand3, sendCommand4, sendCommand5;


    private int deviceWidth;
    private int deviceHeight;

    private int screenWidth;//小屏幕的宽度
    private int screenHeight;//小屏幕的高度，一般情况下，高度等于宽度

    private int gridViewWidth;
    private int gridViewHeight;

    private boolean[][] clicked;
    private int columnCount;
    private int rowCount;
    private int row1, row2, column1, column2;
    private int prePosition = -1;
    private String socketIP = "192.168.43.1";
    private int socketPort = 8899;

    public AppUtils appUtils;
    private SharedPreferences appInfo;
    private int planIndex = 0;
    private byte willbeSendData[] = new byte[16];
    private byte devFunction = (byte) 0x35;
    private List<ReservePlan> spinnerData;
    private ReservePlanDao planDao;
    private SpinnerArrayAdapter spinnerArrayAdapter;

    private TextWatcher textWatcher1, textWatcher2, textWatcher3, textWatcher4, textWatcher5;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ipAddress = (EditText) findViewById(R.id.ip_address);
        port = (EditText) findViewById(R.id.port);
        gridView = (GridView) findViewById(R.id.screen_spread);
        runPlan = (MyButton) findViewById(R.id.run_plan);
        saveIpPort = (Button) findViewById(R.id.save_ip_port);
        spinnerPlan = (Spinner) findViewById(R.id.reserve_plan);
        planName = (EditText) findViewById(R.id.plan_name);
        saveName = (Button) findViewById(R.id.save_name);
        command1 = (EditText) findViewById(R.id.command_text_1);
        sendCommand1 = (Button) findViewById(R.id.send_1);
        command2 = (EditText) findViewById(R.id.command_text_2);
        sendCommand2 = (Button) findViewById(R.id.send_2);
        command3 = (EditText) findViewById(R.id.command_text_3);
        sendCommand3 = (Button) findViewById(R.id.send_3);
        command4 = (EditText) findViewById(R.id.command_text_4);
        sendCommand4 = (Button) findViewById(R.id.send_4);
        command5 = (EditText) findViewById(R.id.command_text_5);
        sendCommand5 = (Button) findViewById(R.id.send_5);

        saveIpPort.setOnClickListener(this);
        runPlan.setOnClickListener(this);
        saveName.setOnClickListener(this);
        textWatcher1 = new HexTextWatcher(command1);
        textWatcher2 = new HexTextWatcher(command2);
        textWatcher3 = new HexTextWatcher(command3);
        textWatcher4 = new HexTextWatcher(command4);
        textWatcher5 = new HexTextWatcher(command5);

        sendCommand1.setOnClickListener(this);
        command1.addTextChangedListener(textWatcher1);
        sendCommand2.setOnClickListener(this);
        command2.addTextChangedListener(textWatcher2);
        sendCommand3.setOnClickListener(this);
        command3.addTextChangedListener(textWatcher3);
        sendCommand4.setOnClickListener(this);
        command4.addTextChangedListener(textWatcher4);
        sendCommand5.setOnClickListener(this);
        command5.addTextChangedListener(textWatcher5);

        init();
        initDbHelp();
        //gridview 区域保持为机器宽度的0.9倍
        gridViewWidth = (int) (deviceWidth * 0.5);
        if (appUtils.checkDeviceHasNavigationBar(this)) {
            gridViewHeight = (int) (deviceWidth * 0.5);
        } else {
            gridViewHeight = (int) (deviceWidth * 0.5);
        }
        //设置gridview为一个正方形区域
        gridView.setLayoutParams(new LinearLayout.LayoutParams(gridViewWidth, gridViewWidth));

        adapter = new RectGridViewAdapter(this, adapterModel);

        initArray(rowCount, columnCount);

        gridView.setNumColumns(3);
        gridView.setAdapter(adapter);
        gridView.setOnItemClickListener(this);

        //设置gridview为一个正方形区域
        gridView.setLayoutParams(new LinearLayout.LayoutParams(gridViewWidth, gridViewHeight));

        adapter = new RectGridViewAdapter(this, adapterModel);

        gridView.setNumColumns(columnCount);
        gridView.setAdapter(adapter);
        gridView.setOnItemClickListener(this);

        spinnerRow = (Spinner) findViewById(R.id.spinnerL);
        spinnerColumn = (Spinner) findViewById(R.id.spinnerW);

        spinnerRow.setOnItemSelectedListener(new SpinnerItemSelectListenerRow());
        spinnerColumn.setOnItemSelectedListener(new SpinnerItemSelectListenerColumn());

        spinnerRow.setSelection(rowCount - 1);
        spinnerColumn.setSelection(columnCount - 1);

        spinnerArrayAdapter = new SpinnerArrayAdapter(this, spinnerData);
        spinnerPlan.setAdapter(spinnerArrayAdapter);
        spinnerPlan.setOnItemSelectedListener(new PlanNumSpinnerListener());
    }

    private void init() {
        appUtils = AppUtils.getIntance(this);

        appInfo = getSharedPreferences("app_info", MODE_PRIVATE);
        columnCount = appInfo.getInt("column", 3);
        rowCount = appInfo.getInt("row", 3);
        socketIP = appInfo.getString("ip", "192.168.43.1");
        socketPort = appInfo.getInt("port", 5000);
        ipAddress.setText(socketIP);
        port.setText(String.valueOf(socketPort));
        appUtils.setIpPort(socketIP, socketPort);

        command1.setText(appInfo.getString("command1",""));
        command2.setText(appInfo.getString("command2",""));
        command3.setText(appInfo.getString("command3",""));
        command4.setText(appInfo.getString("command4",""));
        command5.setText(appInfo.getString("command5",""));

        clicked = new boolean[rowCount][columnCount];

        initArray(rowCount, columnCount);

        row1 = 0;
        column1 = 0;
        row2 = 0;
        column2 = 0;

        DisplayMetrics displayMetrics = getResources().getDisplayMetrics();
        deviceWidth = displayMetrics.widthPixels;
        deviceHeight = displayMetrics.heightPixels;

        screenWidth = (int) (deviceWidth * 0.95) / columnCount;
        screenHeight = (int) (deviceWidth * 0.95) / rowCount;

        adapterModel = new AdapterModel();
        adapterModel.setRow(rowCount);
        adapterModel.setColumn(columnCount);
        adapterModel.setGridViewWidth(gridViewWidth);
        adapterModel.setClicked(clicked);
    }

    private void initArray(int row, int column) {
        for (int i = 0; i < row; i++) {
            for (int j = 0; j < column; j++) {
                clicked[i][j] = false;
            }
        }
    }

    private void initAdapter() {
        initArray(rowCount, columnCount);
        prePosition = -1;
    }

    private void initDbHelp() {
        DaoMaster.DevOpenHelper helper = new DaoMaster.DevOpenHelper(this, "recluse-db", null);
        SQLiteDatabase db = helper.getWritableDatabase();
        DaoMaster daoMaster = new DaoMaster(db);
        DaoSession daoSession = daoMaster.newSession();
        planDao = daoSession.getReservePlanDao();
        initDB();
        spinnerData = planDao.loadAll();
    }

    private void initDB() {
        List<ReservePlan> list = planDao.loadAll();
        if (list.size() != 64) {
            planDao.deleteAll();
            for (int i = 0; i < 64; i++) {
                Log.d(TAG, "initDB333: " + i);
                ReservePlan reservePlan = new ReservePlan(i, i, String.valueOf(i));
                planDao.insert(reservePlan);
            }
        }
        List<ReservePlan> newList = planDao.loadAll();
        Log.d(TAG, "initDB: " + newList.size());
        Log.d(TAG, "initDB 4444: " + newList.get(0).getName());
    }


    private void setAdapterModelData() {
        AdapterModel model = new AdapterModel();
        model.setRow(rowCount);
        model.setColumn(columnCount);
        model.setGridViewHeight(gridViewHeight);
        model.setGridViewWidth(gridViewWidth);
        model.setClicked(clicked);

        adapter.setModel(model);
        adapter.notifyDataSetChanged();
    }

    private void setAreaData(int row1, int column1, int row2, int column2) {
        Log.d(TAG, "start: row1=" + row1 + ", column1 = " + column1);
        Log.d(TAG, "end: row2=" + row2 + ", column2 = " + column2);
        byte var1, var2;
        if (column1 < column2) {
            if (row1 < row2) {
                var1 = (byte) ((row1 + 1) * 16 + column1 + 1);
                var2 = (byte) ((row2 + 1) * 16 + column2 + 1);
            } else {
                var1 = (byte) ((row2 + 1) * 16 + column1 + 1);
                var2 = (byte) ((row1 + 1) * 16 + column2 + 1);
            }

        } else {
            if (row1 < row2) {
                var1 = (byte) ((row1 + 1) * 16 + column2 + 1);
                var2 = (byte) ((row2 + 1) * 16 + column1 + 1);
            } else {
                var1 = (byte) ((row2 + 1) * 16 + column2 + 1);
                var2 = (byte) ((row1 + 1) * 16 + column1 + 1);
            }
        }
        appUtils.setStartEnd(var1, var2);
    }

    private boolean iContainTrues(boolean data[][]) {
        if (data == null) {
            return false;
        }

        if (data.length == 1) {
            return false;
        }

        int count = 0;
        for (int i = 0; i < rowCount; i++) {
            for (int j = 0; j < columnCount; j++) {
                if (data[i][j]) {
                    count++;
                }
            }
        }

        if (count <= 1) {
            return false;
        } else {
            return true;
        }
    }

    private class SpinnerItemSelectListenerRow implements AdapterView.OnItemSelectedListener {

        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            rowCount = position + 1;

            clicked = new boolean[rowCount][columnCount];
            initArray(rowCount, columnCount);

            screenHeight = gridViewHeight / rowCount;
            screenWidth = gridViewWidth / columnCount;
            setAdapterModelData();
            appInfo = getSharedPreferences("app_info", MODE_PRIVATE);
            SharedPreferences.Editor appEditor = appInfo.edit();
            appEditor.putInt("row", rowCount);
            appEditor.commit();
        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {

        }
    }

    private class SpinnerItemSelectListenerColumn implements AdapterView.OnItemSelectedListener {

        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            columnCount = position + 1;
            clicked = new boolean[rowCount][columnCount];
            initArray(rowCount, columnCount);

            gridView.setNumColumns(columnCount);

            screenHeight = gridViewHeight / rowCount;
            screenWidth = gridViewWidth / columnCount;

            setAdapterModelData();
            appInfo = getSharedPreferences("app_info", MODE_PRIVATE);
            SharedPreferences.Editor appEditor = appInfo.edit();
            appEditor.putInt("column", columnCount);
            appEditor.commit();
        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {
        }
    }

    class PlanNumSpinnerListener implements AdapterView.OnItemSelectedListener {

        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
//            planIndex = position + 1;

            planName.setText(spinnerData.get(position).getName());

            willbeSendData[0] = (byte) 0x0e;
            willbeSendData[1] = (byte) 0x01;
            willbeSendData[2] = devFunction;
            willbeSendData[3] = (byte) (position);
//            willbeSendData[3] = (byte) (planIndex + 1);
            willbeSendData[4] = (byte) 0x00;
            willbeSendData[5] = (byte) 0x00;
            willbeSendData[6] = (byte) 0x00;
            willbeSendData[7] = (byte) 0x00;
            willbeSendData[8] = (byte) 0x00;
            willbeSendData[9] = (byte) 0x00;
            willbeSendData[10] = (byte) 0x00;
            willbeSendData[11] = (byte) 0x00;
            willbeSendData[12] = (byte) 0x00;
            willbeSendData[13] = (byte) 0x00;
            willbeSendData[14] = (byte) 0x00;

            int sum = 0;
            for (int i = 0; i < willbeSendData.length - 2; i++) {
                sum += willbeSendData[i];
            }

            willbeSendData[15] = (byte) sum;
        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {

        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Log.e(TAG, "onItemClick position: " + position);

        RectGridViewAdapter.ViewHolder holder = (RectGridViewAdapter.ViewHolder) view.getTag();

        int r = position / columnCount;
        int c = position % columnCount;

        if (iContainTrues(clicked)) {
            initAdapter();
            adapter.notifyDataSetChanged();
        }

        if (clicked[r][c]) {
            clicked[r][c] = false;
            holder.screenView.setBackgroundColor(Color.GRAY);
            return;
        }

        if (prePosition != -1) {
            row1 = prePosition / columnCount;
            column1 = prePosition % columnCount;
        }

        row2 = position / columnCount;
        column2 = position % columnCount;

        if (position > prePosition && prePosition != -1) {
            for (int i = row1; i < row2 + 1; i++) {
                if (column2 > column1) {
                    appUtils.setClickedInit(row1, column1, row2, column2);
                    for (int j = column1; j < column2 + 1; j++) {
                        clicked[i][j] = true;
                    }
                } else {
                    appUtils.setClickedInit(row1, column2, row2, column1);
                    for (int j = column2; j < column1 + 1; j++) {
                        clicked[i][j] = true;
                    }
                }
            }
            prePosition = position;
            setAreaData(row1, column1, row2, column2);
        } else if (position < prePosition) {
            appUtils.setClickedInit(row2, column1, row1, column2);
            for (int i = row2; i < row1 + 1; i++) {
                if (column1 < column2) {
                    for (int j = column1; j < column2 + 1; j++) {
                        clicked[i][j] = true;
                    }
                } else {
                    for (int j = column2; j < column1 + 1; j++) {
                        clicked[i][j] = true;
                    }
                }
            }
            prePosition = position;
            setAreaData(row1, column1, row2, column2);
        } else {
            if (clicked[r][c]) {
                clicked[r][c] = false;
            } else {
                clicked[r][c] = true;
                byte var = (byte) ((r + 1) * 16 + c + 1);
                appUtils.setStartEnd(var, var);
            }
            prePosition = position;
            if (clicked[r][c]) {
                holder.screenView.setBackgroundColor(Color.RED);
            } else {
                holder.screenView.setBackgroundColor(Color.GRAY);
            }
        }
        adapter.notifyDataSetChanged();
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.run_plan:
                byte index = (byte) planIndex;
                appUtils.sendMessage((byte) 0x15, index);
                willbeSendData[2] = (byte) 0x15;
                int sum = 0;
                for (int i = 0; i < willbeSendData.length - 2; i++) {
                    sum += willbeSendData[i];
                }

                willbeSendData[15] = (byte) sum;
                break;
            case R.id.save_name:
                String name = planName.getText().toString();
                if (!name.isEmpty() && name.length() < 6) {
                    Log.e(TAG,"PLAY index: "+planIndex);
                    ReservePlan plan = new ReservePlan(planIndex, planIndex, name);
                    planDao.update(plan);
                    spinnerData = planDao.loadAll();
                    spinnerArrayAdapter.setPlanData(spinnerData);
                    Toast.makeText(this, "已保存新的预案名称！", Toast.LENGTH_SHORT).show();
                    spinnerArrayAdapter.notifyDataSetChanged();
                } else {
                    Toast.makeText(this, "输入名称为空或者超过5个字符", Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.save_ip_port:
                try {
                    appInfo = getSharedPreferences("app_info", MODE_PRIVATE);
                    SharedPreferences.Editor editor = appInfo.edit();
                    socketIP = ipAddress.getText().toString();
                    socketPort = Integer.parseInt(port.getText().toString());
                    editor.putString("ip", socketIP);
                    editor.putInt("port", socketPort);
                    editor.commit();
                    appUtils.setIpPort(socketIP, socketPort);
                } catch (Exception e) {
                    Log.e(TAG, e.getMessage());
                }
                Toast.makeText(this, "已保存IP地址和端口号", Toast.LENGTH_SHORT).show();
                break;
            case R.id.send_1:
                String input1 = command1.getText().toString();
                if(input1.isEmpty()){
                    Toast.makeText(this, "数据为空", Toast.LENGTH_SHORT).show();
                    return;
                }
                appUtils.send(getBytesFromInput(input1));
                saveCommand("command1", input1);
                break;
            case R.id.send_2:
                String input2 = command2.getText().toString();
                if(input2.isEmpty()){
                    Toast.makeText(this, "数据为空", Toast.LENGTH_SHORT).show();
                    return;
                }
                appUtils.send(getBytesFromInput(input2));
                saveCommand("command2", input2);
                break;
            case R.id.send_3:
                String input3 = command3.getText().toString();
                if(input3.isEmpty()){
                    Toast.makeText(this, "数据为空", Toast.LENGTH_SHORT).show();
                    return;
                }
                appUtils.send(getBytesFromInput(input3));
                saveCommand("command3", input3);
                break;
            case R.id.send_4:
                String input4 = command4.getText().toString();
                if(input4.isEmpty()){
                    Toast.makeText(this, "数据为空", Toast.LENGTH_SHORT).show();
                    return;
                }
                appUtils.send(getBytesFromInput(input4));
                saveCommand("command4", input4);
                break;
            case R.id.send_5:
                String input5 = command5.getText().toString();
                if(input5.isEmpty()){
                    Toast.makeText(this, "数据为空", Toast.LENGTH_SHORT).show();
                    return;
                }
                appUtils.send(getBytesFromInput(input5));
                saveCommand("command5", input5);
                break;
        }
    }

    private void saveCommand(String key, String value){
        appInfo = getSharedPreferences("app_info", MODE_PRIVATE);
        SharedPreferences.Editor editor = appInfo.edit();
        editor.putString(key, value);
        editor.commit();
    }

    private byte[] getBytesFromInput(String input) {
        Log.d(TAG, "getBytesFromInput: " + input);
        String temp[] = input.split(" ");
        return hexStringToBytes(temp);
    }

    public byte[] hexStringToBytes(String[] hexString) {
        if (hexString.length == 0) {
            return null;
        }
        int length = hexString.length;
        byte[] d = new byte[length];
        for (int i = 0; i < length; i++) {
            int temp = Integer.parseInt(hexString[i], 16);
            d[i] = (byte) temp;
        }
        return d;
    }
}


