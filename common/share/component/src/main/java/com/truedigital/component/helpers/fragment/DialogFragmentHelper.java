package com.truedigital.component.helpers.fragment;

import android.os.Handler;

import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

/**
 * Created by ppy at somewhere with someone.
 */

public class DialogFragmentHelper {

    private static boolean isBlock = false;
    private static final Handler mHandler = new Handler();
    private static final Runnable mRunnable = () -> isBlock = false;

    public static void replaceWithAnimation(
            FragmentManager manager,
            DialogFragment fragment,
            String tag,
            boolean addToBackStack
    ) {
        if (!isBlock) {
            isBlock = true;
            FragmentTransaction transaction = manager.beginTransaction();
            Fragment prev = manager.findFragmentByTag(tag);
            if (prev != null) {
                transaction.remove(prev);
            }
            if (addToBackStack) {
                transaction.addToBackStack(null);
            }
            transaction.commitAllowingStateLoss();
            mHandler.postDelayed(mRunnable, 100);
            try {
                fragment.show(manager, tag);
            } catch (IllegalStateException e) {
                // Do nothing
            } catch (Exception e) {
                // Do nothing
            }
        }
    }

    public static void clearDialog(FragmentManager fm) {
        if (fm != null) {
            int backStackCount = fm.getBackStackEntryCount();
            if (backStackCount > 0) {
                for (int i = 0; i < backStackCount; i++) {
                    FragmentManager.BackStackEntry entry = fm.getBackStackEntryAt(i);
                    int id = entry.getId();
                    Fragment fragment = fm.findFragmentById(id);
                    if (fragment instanceof DialogFragment) {
                        ((DialogFragment) fragment).dismiss();
                    }
                }
            }
        }
    }
}
