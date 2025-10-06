package com.example.abamailapp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.preference.PreferenceManager;

import com.example.abamailapp.entities.Label;
import com.example.abamailapp.entities.User;
import com.example.abamailapp.viewmodels.LabelViewModel;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.android.material.navigation.NavigationView;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private DrawerLayout drawerLayout;
    private AppDB db;
    private NavigationView navigationView;
    private NavigationView profileDrawer;
    private ImageView profileButton;

    private List<Label> cachedLabels = new ArrayList<>();

    private static final String PREF_DARK_MODE = "pref_dark_mode";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d("DEBUG_THEME", "first line of onCreate - localNightMode=" + getDelegate().getLocalNightMode());
        // Apply saved theme before calling super.onCreate to avoid flicker
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        if (!prefs.contains(PREF_DARK_MODE)) {
            // ensure new users have an explicit saved pref
            prefs.edit().putBoolean(PREF_DARK_MODE, false).apply();
        }
        boolean dark = prefs.getBoolean(PREF_DARK_MODE, false);
        getDelegate().setLocalNightMode(dark ? AppCompatDelegate.MODE_NIGHT_YES : AppCompatDelegate.MODE_NIGHT_NO);

        super.onCreate(savedInstanceState);
        getDelegate().applyDayNight();

        setContentView(R.layout.activity_main);

        db = DatabaseClient.getInstance(this);

        MailDao mailDao = db.mailDao();
        UserDao userDao = db.userDao();


        // Toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // DrawerLayout and NavigationView and profile drawer
        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.navigation_view);
        // Profile button and drawer
        profileDrawer = findViewById(R.id.profile_drawer);
        profileButton = findViewById(R.id.profile_button);
        // Open profile drawer
        profileButton.setOnClickListener(v -> {
            drawerLayout.openDrawer(GravityCompat.END);
        });
        // Profile drawer header views
        View headerView = profileDrawer.getHeaderView(0);
        TextView usernameText = headerView.findViewById(R.id.profile_header_name);
        TextView emailText = headerView.findViewById(R.id.profile_header_email);
        ImageView profileImage = headerView.findViewById(R.id.profile_header_image);
        // Load user info
        int userId = SessionManager.getLoggedInUserId();
        if (userId != -1) {
            User user = db.userDao().getUserById(userId);
            if (user != null) {
                usernameText.setText(user.getUserName());
                emailText.setText(user.getMailAddress());

                if (user.getImage() != null && !user.getImage().equals("default")) {
                    Bitmap bmp = decodeBase64Image(user.getImage());
                    if (bmp != null) {
                        profileImage.setImageBitmap(bmp);
                        profileButton.setImageBitmap(bmp);
                    } else {
                        profileImage.setImageResource(R.drawable.ic_default_user);
                    }
                } else {
                    profileImage.setImageResource(R.drawable.ic_default_user);
                }
            }
        }
        // Dark mode switch in profile drawer
        try {
            MenuItem darkItem = profileDrawer.getMenu().findItem(R.id.action_dark_mode);
            if (darkItem != null) {
                View actionView = darkItem.getActionView();
                if (actionView != null) {
                    com.google.android.material.switchmaterial.SwitchMaterial darkSwitch =
                            actionView.findViewById(R.id.dark_mode_switch);

                    // Temporarily remove listener while we initialize the switch state
                    darkSwitch.setOnCheckedChangeListener(null);

                    // Initialize checked state from prefs (no callback fired)
                    darkSwitch.setChecked(dark);

                    // Now attach listener
                    darkSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
                        // Save preference immediately
                        prefs.edit().putBoolean(PREF_DARK_MODE, isChecked).apply();
                        // Determine target modes
                        final int targetGlobal = isChecked ? AppCompatDelegate.MODE_NIGHT_YES : AppCompatDelegate.MODE_NIGHT_NO;
                        final int currentGlobal = AppCompatDelegate.getDefaultNightMode();
                        // Update global default only if different (ensures new Activities get correct theme)
                        if (currentGlobal != targetGlobal) {
                            AppCompatDelegate.setDefaultNightMode(targetGlobal);
                        }

                        // Apply to THIS activity immediately using local delegate to avoid a full app recreation
                        getDelegate().setLocalNightMode(targetGlobal);
                        // Apply the change right away and minimize flicker
                        getDelegate().applyDayNight();
                        Log.d("DEBUG_THEME", "after apply- localNightMode=" + getDelegate().getLocalNightMode());
                    });
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }


        LabelViewModel labelViewModel = new ViewModelProvider(this).get(LabelViewModel.class);

        NavigationView navigationView = findViewById(R.id.navigation_view);
        Menu menu = navigationView.getMenu();

        labelViewModel.refresh();
        labelViewModel.getLabels().observe(this, labels -> {
            cachedLabels.clear();
            cachedLabels.addAll(labels);
            // Clear any existing dynamic labels first
            int startIndex = 4;
            while (menu.size() > startIndex) {
                menu.removeItem(menu.getItem(startIndex).getItemId());
            }

            // Add labels after spam
            for (Label label : labels) {
                menu.add(Menu.NONE, label.getId(), Menu.NONE, label.getName())
                        .setIcon(R.drawable.ic_label);
            }
        });

        Button createLabel = findViewById(R.id.btn_add_label);
        createLabel.setOnClickListener(v -> {
            Intent intent = new Intent(this, AddLabelActivity.class);
            startActivity(intent);
        });



        ImageView hamburgerButton = findViewById(R.id.hamburger_button);
        hamburgerButton.setOnClickListener(v -> drawerLayout.openDrawer(GravityCompat.START));

        // Handle navigation menu clicks
        navigationView.setNavigationItemSelectedListener(item -> {
            Fragment selected = null;
            int id = item.getItemId();

            if (id == R.id.nav_inbox) {
                selected = new InboxFragment();
            } else if (id == R.id.nav_sent) {
                selected = new SentFragment();
            } else if (id == R.id.nav_drafts) {
                selected = new DraftFragment();
            } else if (id == R.id.nav_spam) {
                selected = new SpamFragment();
            } else {
                // Handle dynamic labels
                Label clickedLabel = null;
                if (cachedLabels != null) {
                    for (Label label : cachedLabels) {
                        if (label.getId() == id) {
                            clickedLabel = label;
                            break;
                        }
                    }
                }

                if (clickedLabel != null) {
                    selected = LabelMailsFragment.newInstance(clickedLabel.getBackendId());
                }
            }

            if (selected != null) {
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.container, selected)
                        .commit();
            }

            drawerLayout.closeDrawers();
            return true;
        });
        // Handle profile drawer clicks
        profileDrawer.setNavigationItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.action_logout) {
                SessionManager.clearSession();
                // Go back to login
                Intent intent = new Intent(MainActivity.this, Login.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                finish();
                return true;
            } else if (id == R.id.action_manage_labels) {
                Intent intent = new Intent(MainActivity.this, ManageLabelsActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                return true;
            } else if (id == R.id.action_dark_mode) {
                // Handled by switch
                return true;
            }
                return false;
        });

        // Default fragment
        if (savedInstanceState == null) {
            navigationView.setCheckedItem(R.id.nav_inbox);
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.container, new InboxFragment())
                    .commit();
        }

        EditText searchInput = findViewById(R.id.search_input);

        searchInput.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                String query = searchInput.getText().toString().trim();

                if (!query.isEmpty()) {
                    // Pass query to your SearchFragment
                    Bundle bundle = new Bundle();
                    bundle.putString("query", query);

                    SearchFragment searchFragment = new SearchFragment();
                    searchFragment.setArguments(bundle);

                    getSupportFragmentManager().beginTransaction()
                            .replace(R.id.container, searchFragment) // adjust to your container id
                            .addToBackStack(null)
                            .commit();
                }
                return true;
            }
            return false;
        });


        ExtendedFloatingActionButton fab = findViewById(R.id.compose_btn);
        fab.setOnClickListener(v -> {
            Intent intent = new Intent(this, ComposeActivity.class);
            startActivity(intent);
        });

    }
    // Decode Base64 image for profile
    public Bitmap decodeBase64Image(String base64) {
        try {
            // Remove the data:image/*;base64, prefix if exists
            if (base64.startsWith("data:image")) {
                base64 = base64.substring(base64.indexOf(",") + 1);
            }
            byte[] decodedBytes = Base64.decode(base64, Base64.DEFAULT);
            return BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.length);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    // Dark mode helpers
    private void applySavedTheme() {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        boolean dark = prefs.getBoolean(PREF_DARK_MODE, false);
        AppCompatDelegate.setDefaultNightMode(
                dark ? AppCompatDelegate.MODE_NIGHT_YES : AppCompatDelegate.MODE_NIGHT_NO
        );
    }
    private void saveDarkModePref(boolean dark) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        prefs.edit().putBoolean(PREF_DARK_MODE, dark).apply();
    }
}
