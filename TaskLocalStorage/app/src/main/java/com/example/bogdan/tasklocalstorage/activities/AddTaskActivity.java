package com.example.bogdan.tasklocalstorage.activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;

import com.example.bogdan.tasklocalstorage.R;

public class AddTaskActivity extends AppCompatActivity {

    public static final String DESCRIPTION_REPLY = "DESCRIPTION";
    public static final String PRIORITY_REPLY = "PRIORITY";

    private EditText mDescriptionEditText;
    private EditText mPriorityEditText;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_task);

        mDescriptionEditText = (EditText)findViewById(R.id.edit_description);
        mPriorityEditText = (EditText)findViewById(R.id.edit_priority);

        final Button button = (Button) findViewById(R.id.button_save);

        button.setOnClickListener((view)->{

            Intent replyIntent = new Intent();
            if (TextUtils.isEmpty(mDescriptionEditText.getText()) || TextUtils.isEmpty(mPriorityEditText.getText())) {
                setResult(RESULT_CANCELED, replyIntent);
            } else {
                String description = mDescriptionEditText.getText().toString();
                String priority = mPriorityEditText.getText().toString();

                replyIntent.putExtra(DESCRIPTION_REPLY, description);
                replyIntent.putExtra(PRIORITY_REPLY, priority);

                setResult(RESULT_OK, replyIntent);
            }

            finish();

        });
    }
}
