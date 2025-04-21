package hku.cs.notebook;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.Menu;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.navigation.NavigationView;

import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.AppCompatActivity;

import hku.cs.notebook.databinding.ActivityMainBinding;
import hku.cs.notebook.ui.chat.ChatActivity;
import hku.cs.notebook.ui.login.LoginActivity;

public class MainActivity extends AppCompatActivity {

    private AppBarConfiguration mAppBarConfiguration;
    private ActivityMainBinding binding;
    private static final String PREF_NAME = "NotebookPrefs";
    private static final String KEY_USER_ID = "userId";
    private static final String KEY_USERNAME = "username";
    private static final String KEY_USEREMAIL = "userEmail";
    private static final int LOGIN_REQUEST_CODE = 1001;

    @Override
    protected void onResume() {
        super.onResume();

        checkLoginStatus();
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.appBarMain.toolbar);
        binding.appBarMain.fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, ChatActivity.class);
                startActivity(intent);
            }
        });
        DrawerLayout drawer = binding.drawerLayout;
        NavigationView navigationView = binding.navView;
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_home)
                .setOpenableLayout(drawer)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);

        // 检查用户是否已登录
        checkLoginStatus();
    }

    private void checkLoginStatus() {
        SharedPreferences prefs = getSharedPreferences(PREF_NAME, MODE_PRIVATE);
        String userIdStr = prefs.getString(KEY_USER_ID, "-1");
        int userId = Integer.parseInt(userIdStr);


        if (userId == -1) {
            // 用户未登录，跳转到登录页面
            Intent intent = new Intent(MainActivity.this, LoginActivity.class);
            startActivity(intent);
        } else {
            // 用户已登录，更新UI显示用户名
            String username = prefs.getString(KEY_USERNAME, "");
            String userEmail = prefs.getString(KEY_USEREMAIL, "");
            Log.d("updateUIWithUserInfo", "2");
            updateUIWithUserInfo(username, userEmail);
        }
    }

    public void updateUIWithUserInfo(String username, String userEmail) {
        // Log for debugging
        Log.d("updateUIWithUserInfo", "Username: " + username + ", Email: " + userEmail);
        // Find the NavigationView
        NavigationView navigationView = this.findViewById(R.id.nav_view);
        // Get the header view
        View headerView = navigationView.getHeaderView(0);

        // Find the TextViews in the header layout
        TextView usernameTextView = headerView.findViewById(R.id.username);
        TextView userEmailTextView = headerView.findViewById(R.id.user_email);

        usernameTextView.setText(username); // Set the username
        userEmailTextView.setText(userEmail); // Set the email
    }



    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {
            // 登录成功，获取用户信息
            String userId = data.getStringExtra("userId");
            String username = data.getStringExtra("username");
            String userEmail = data.getStringExtra("userEmail");

            // 保存用户信息到SharedPreferences
            SharedPreferences prefs = getSharedPreferences(PREF_NAME, MODE_PRIVATE);
            SharedPreferences.Editor editor = prefs.edit();
            editor.putString(KEY_USER_ID, userId);
            editor.putString(KEY_USERNAME, username);
            editor.putString(KEY_USEREMAIL, userEmail);
            editor.apply();

            // 更新UI
            Log.d("updateUIWithUserInfo", "1");
            updateUIWithUserInfo(username, userEmail);
        }
        else if (requestCode == LOGIN_REQUEST_CODE && resultCode == RESULT_CANCELED) {
            // 用户取消登录，可以选择关闭应用或显示提示
            Snackbar.make(binding.getRoot(), "Login required to use the app", Snackbar.LENGTH_LONG).show();
            // 可以选择在这里再次启动登录活动或关闭应用
            // finish();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }

    private void performLogout() {
        // 清除用户登录数据
        SharedPreferences prefs = getSharedPreferences(PREF_NAME, MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.remove(KEY_USER_ID);
        editor.remove(KEY_USERNAME);
        editor.remove(KEY_USEREMAIL);
        editor.apply();
        Log.d("updateUIWithUserInfo", "3");
        updateUIWithUserInfo("username", "userEmail");


        // 显示提示信息（可选）
        Snackbar.make(binding.getRoot(), "You have logged out successfully", Snackbar.LENGTH_LONG).show();

        // 跳转回登录界面并清除当前活动栈
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.log_out) {
            performLogout(); // 执行退出功能
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}