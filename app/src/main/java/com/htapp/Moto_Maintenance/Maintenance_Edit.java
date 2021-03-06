package com.htapp.Moto_Maintenance;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.SpannableStringBuilder;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

@RequiresApi(api = Build.VERSION_CODES.KITKAT)
public class Maintenance_Edit extends AppCompatActivity {

    EditText showDate;
    EditText odometer;
    EditText comment;
    Spinner maintenancelist;
    String moto_data;
    int num;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.maintenance_edit);

        findViewById(R.id.showDate).setOnClickListener(button_calendar);
        showDate = (EditText) findViewById(R.id.showDate);
        odometer = (EditText) findViewById(R.id.odometer2);
        comment = (EditText) findViewById(R.id.comment);
        maintenancelist = (Spinner) findViewById(R.id.maintenancelist2);
        findViewById(R.id.edit).setOnClickListener(button_edit);

        Intent intent = getIntent();
        moto_data = intent.getStringExtra(Maintenance_Data_Activity.MOTO_DATA2);
        num = intent.getIntExtra("MENTE_NUM",0);

        final String[] day = readFile(moto_data + "day.txt");
        final String[] odo = readFile(moto_data + "odo.txt");
        final String[] cont = readFile(moto_data + "cont.txt");
        final String[] comment2 = readFile(moto_data + "comment.txt");

        int spn = spn_pos(cont[num]);

        String[] strs = comment2[num].split("~/");
        StringBuilder sb = new StringBuilder();
        for (int i = 0 ; i < strs.length ; i++){
            sb.append(strs[i] + "\n");
        }
        String comment3 = new String(sb);

        showDate.setText(day[num]);
        odometer.setText(odo[num]);
        maintenancelist.setSelection(spn);
        comment.setText(comment3);
    }

    View.OnClickListener button_calendar = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            final Calendar date = Calendar.getInstance();
            //DatePickerDialog???????????????????????????
            DatePickerDialog datePickerDialog = new DatePickerDialog(
                    Maintenance_Edit.this,
                    new DatePickerDialog.OnDateSetListener() {
                        @Override
                        public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                            //set?????????????????????????????????
                            showDate.setText(String.format("%d ??? %02d ??? %02d ???", year, month + 1, dayOfMonth));
                        }
                    },
                    date.get(Calendar.YEAR),
                    date.get(Calendar.MONTH),
                    date.get(Calendar.DATE)
            );
            //dialog?????????
            datePickerDialog.show();
        }
    };

    //???????????????
    View.OnClickListener button_edit = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

            String time = showDate.getText().toString();
            String odo = odometer.getText().toString();

            SpannableStringBuilder sp = (SpannableStringBuilder)comment.getText();
            String comment2 = sp.toString();

            String[] strs = comment2.split("\n");
            StringBuilder sb = new StringBuilder();
            for (int i = 0 ; i < strs.length ; i++){
                sb.append(strs[i] + "~/");
            }
            String comment3 = new String(sb);

            if (time.equals("") || odo.equals("")) {

                alart_noneinput();

            }else if(comment3.contains(",")){

                alart_dot();

            } else {

                alert_Edit();

            }
        }
    };





    //??????????????????


    public void alart_dot() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("??????????????? , ??????????????????????????????????????????")
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // ?????????????????????????????????????????????
                    }
                });
        builder.show();
    }

    //????????????????????????????????????????????????????????????????????????????????????????????????????????????
    public void edit (String file, String newtxt , int num){

        String[] str = readFile(file);

        List<String> list = new ArrayList<String>(Arrays.asList(str));
        list.set(num,newtxt);

        String[] str2 = (String[]) list.toArray(new String[list.size()]);

        try (FileOutputStream fo = openFileOutput(file, Context.MODE_PRIVATE);) {
            OutputStreamWriter osw = new OutputStreamWriter(fo, "UTF-8");
            PrintWriter pw = new PrintWriter(osw);

            StringBuilder sb = new StringBuilder();

            for(int count = str2.length ; count > 0 ; count--) {

                sb.insert(0,",");
                sb.insert(0, str2[count - 1]);

            }

            pw.println(sb.toString());
            pw.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public void alart_noneinput() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("???????????????????????????????????????????????????")
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // ?????????????????????????????????????????????
                    }
                });
        builder.show();
    }

    //?????????????????????
    public void alert_Edit(){

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("??????");
        builder.setMessage("????????????????????????????????????");
        builder.setPositiveButton("??????", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                String time = showDate.getText().toString();
                String odo = odometer.getText().toString();
                String cont = maintenancelist.getSelectedItem().toString();

                Intent intent2 = getIntent();
                int num2 = intent2.getIntExtra("NUM4",0);

                String strnowodo = (readFile("no.txt")[num2]);

                if(!strnowodo.equals(" ")) {
                    int odoi = Integer.parseInt(odo);
                    int odon = Integer.parseInt(strnowodo);
                    if (odoi > odon) {
                        edit("no.txt", odo, num2);
                    }
                } else {
                    edit("no.txt", odo, num2);
                }

                Intent intent = getIntent();
                moto_data = intent.getStringExtra(Maintenance_Data_Activity.MOTO_DATA2);

                SpannableStringBuilder sp = (SpannableStringBuilder)comment.getText();
                String comment2 = sp.toString();

                String[] strs = comment2.split("\n");
                StringBuilder sb = new StringBuilder();
                for (int i = 0 ; i < strs.length ; i++){
                    sb.append(strs[i] + "~/");
                }
                String com2 = new String(sb);

                edit(moto_data + "day.txt", time, num);
                edit(moto_data + "odo.txt", odo, num);
                edit(moto_data + "cont.txt", cont, num);
                edit(moto_data + "comment.txt", com2, num);

                Toast.makeText(Maintenance_Edit.this, "??????????????????????????????", Toast.LENGTH_LONG).show();

                finish();

            }
        })
                .setNegativeButton("???????????????", null)
                .show();
    }


    //txt??????????????????????????????String?????????????????????????????????
    public String[] readFile(String file) {

        String str;
        String[] tokens1 = null;

        try (FileInputStream fileInputStream = openFileInput(file);
             BufferedReader reader = new BufferedReader(new InputStreamReader(fileInputStream, StandardCharsets.UTF_8))) {

            String lineBuffer;
            while ((lineBuffer = reader.readLine()) != null) {
                str = lineBuffer;

                tokens1 = str.split(",");

            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return tokens1;
    }


    public int spn_pos(String str){

        int i = 0;

        switch (str) {

            case"???????????????":
                i = 0;
                break;

            case"??????????????????????????????":
                i = 1;
                break;

            case"???????????????":
                i = 2;
                break;

            case"???????????????????????????":
                i = 3;
                break;

            case"???????????????????????????":
                i = 4;
                break;

            case"????????????????????????????????????":
                i = 5;
                break;

            case"???????????????????????????????????????":
                i = 6;
                break;

            case"????????????????????????":
                i = 7;
                break;

            case"????????????????????????????????????":
                i = 8;
                break;

            case"??????????????????????????????":
                i = 9;
                break;

            case"?????????????????????":
                i = 10;
                break;

            case"?????????????????????????????????":
                i = 11;
                break;

            case"??????":
                i = 12;
                break;

            case"????????????(??????)":
                i = 13;
                break;

            case"????????????(??????)":
                i = 14;
                break;

            case"????????????????????????":
                i = 15;
                break;
        }
        return i;
    }
}
