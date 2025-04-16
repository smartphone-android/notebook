package hku.cs.notebook;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.Menu;

import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.navigation.NavigationView;

import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.AppCompatActivity;

import hku.cs.notebook.databinding.ActivityMainBinding;
import hku.cs.notebook.ui.agent.WebViewActivity;
import hku.cs.notebook.ui.chat.ChatActivity;
import hku.cs.notebook.ui.login.LoginActivity;

public class MainActivity extends AppCompatActivity {

    private AppBarConfiguration mAppBarConfiguration;
    private ActivityMainBinding binding;
    private static final String PREF_NAME = "NotebookPrefs";
    private static final String KEY_USER_ID = "userId";
    private static final String KEY_USERNAME = "username";
    private static final int LOGIN_REQUEST_CODE = 1001;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.appBarMain.toolbar);
        binding.appBarMain.fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                Snackbar.make(view, "search button", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null)
//                        .setAnchorView(R.id.fab).show();
                //Intent intent = new Intent(MainActivity.this, WebViewActivity.class);
                Intent intent = new Intent(MainActivity.this, ChatActivity.class);
                startActivity(intent);
            }
        });
        DrawerLayout drawer = binding.drawerLayout;
        NavigationView navigationView = binding.navView;
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_home, R.id.nav_gallery, R.id.nav_slideshow)
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
        int userId = prefs.getInt(KEY_USER_ID, -1);
        
        if (userId == -1) {
            // 用户未登录，跳转到登录页面
            Intent intent = new Intent(MainActivity.this, LoginActivity.class);
            startActivityForResult(intent, LOGIN_REQUEST_CODE);
        } else {
            // 用户已登录，更新UI显示用户名
            String username = prefs.getString(KEY_USERNAME, "");
            updateUIWithUserInfo(username);
        }
    }
    
    private void updateUIWithUserInfo(String username) {
        // 更新导航抽屉中的用户信息
        NavigationView navigationView = binding.navView;
        View headerView = navigationView.getHeaderView(0);
        // 假设导航抽屉头部有显示用户名的TextView，ID为nav_header_username
        // TextView usernameTextView = headerView.findViewById(R.id.nav_header_username);
        // usernameTextView.setText(username);
    }
    
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        
        if (requestCode == LOGIN_REQUEST_CODE && resultCode == RESULT_OK) {
            // 登录成功，获取用户信息
            String userId = data.getStringExtra("userId");
            String username = data.getStringExtra("username");
            
            // 保存用户信息到SharedPreferences
            SharedPreferences prefs = getSharedPreferences(PREF_NAME, MODE_PRIVATE);
            SharedPreferences.Editor editor = prefs.edit();
            editor.putString(KEY_USER_ID, userId);
            editor.putString(KEY_USERNAME, username);
            editor.apply();
            
            // 更新UI
            updateUIWithUserInfo(username);
        } else if (requestCode == LOGIN_REQUEST_CODE && resultCode == RESULT_CANCELED) {
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
}