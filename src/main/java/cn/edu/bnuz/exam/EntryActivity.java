package cn.edu.bnuz.exam;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

public class EntryActivity extends AppCompatActivity implements View.OnClickListener {
    EditText usernameBox;
    ImageView test;
    ImageView engine;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_entry);

        usernameBox = (EditText) findViewById(R.id.username);
        engine = (ImageView) findViewById(R.id.softwareengine);
        test = (ImageView) findViewById(R.id.softwaretesttest);


        engine.setOnClickListener(this);
        test.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        int buttonId = view.getId();
        String username = usernameBox.getText().toString();
        if (username.equals("")) {
            Toast.makeText(this, "请输入用户名！", Toast.LENGTH_SHORT).show();
            return;
        }

        usernameBox = (EditText) findViewById(R.id.username);
        Intent intent = new Intent(EntryActivity.this, MainActivity.class);
        Bundle bundle = new Bundle();
        bundle.putString("username", username);
        switch (buttonId) {
            case R.id.softwaretesttest:
                bundle.putString("exercisename", "软件测试");
                break;
            case R.id.softwareengine:
                bundle.putString("exercisename", "软件工程");
                break;
        }
        intent.putExtras(bundle);
        startActivity(intent);
    }
}
