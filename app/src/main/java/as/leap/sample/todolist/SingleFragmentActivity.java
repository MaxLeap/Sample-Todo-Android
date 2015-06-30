package as.leap.sample.todolist;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;

public abstract class SingleFragmentActivity extends AppCompatActivity {

    protected abstract Fragment createFragment();

    protected Fragment mSingleFragment;

    protected int getLayoutResId() {
        return R.layout.activity_simple_fragment;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(getLayoutResId());

        bindFragment();
    }

    protected void bindFragment() {
        FragmentManager fm = getSupportFragmentManager();
        mSingleFragment = fm
                .findFragmentById(R.id.singleFragmentContainer);
        if (mSingleFragment == null) {
            mSingleFragment = createFragment();
            fm.beginTransaction()
                    .add(R.id.singleFragmentContainer, mSingleFragment)
                    .commit();
        }
    }
}
