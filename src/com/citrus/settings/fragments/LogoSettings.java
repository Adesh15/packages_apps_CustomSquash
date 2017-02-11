/*
 * Copyright (C) 2015 The Dirty Unicorns project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.citrus.settings.fragments;

import android.content.ContentResolver;
import android.content.res.Resources;
import android.database.ContentObserver;
import android.os.Bundle;
import android.os.Handler;
import android.os.UserHandle;
import android.support.v7.preference.ListPreference;
import android.support.v7.preference.Preference;
import android.support.v7.preference.PreferenceScreen;
import android.support.v7.preference.PreferenceCategory;
import android.support.v7.preference.Preference.OnPreferenceChangeListener;
import android.support.v14.preference.SwitchPreference;
import android.provider.Settings;
import android.provider.Settings.SettingNotFoundException;

import com.android.settings.R;
import com.android.internal.logging.MetricsLogger;
import com.android.settings.SettingsPreferenceFragment;
import com.android.settings.Utils;

import com.android.internal.logging.MetricsProto.MetricsEvent;

import net.margaritov.preference.colorpicker.ColorPickerPreference;

public class LogoSettings extends SettingsPreferenceFragment implements OnPreferenceChangeListener {

    public static final String TAG = "LogoSettings";

    private static final String KEY_CITRUS_LOGO_COLOR = "status_bar_citrus_logo_color";
    private static final String KEY_CITRUS_LOGO_STYLE = "status_bar_citrus_logo_style";

    private ColorPickerPreference mCitrusLogoColor;
    private ListPreference mCitrusLogoStyle;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        addPreferencesFromResource(R.xml.citrus_logo);

        PreferenceScreen prefSet = getPreferenceScreen();

        	// Citrus logo color
        	mCitrusLogoColor =
            (ColorPickerPreference) prefSet.findPreference(KEY_CITRUS_LOGO_COLOR);
        	mCitrusLogoColor.setOnPreferenceChangeListener(this);
        	int intColor = Settings.System.getInt(getContentResolver(),
                Settings.System.STATUS_BAR_CITRUS_LOGO_COLOR, 0xffffffff);
       		String hexColor = String.format("#%08x", (0xffffffff & intColor));
            mCitrusLogoColor.setSummary(hexColor);
            mCitrusLogoColor.setNewPreviewColor(intColor);

            mCitrusLogoStyle = (ListPreference) findPreference(KEY_CITRUS_LOGO_STYLE);
            int citrusLogoStyle = Settings.System.getIntForUser(getContentResolver(),
                    Settings.System.STATUS_BAR_CITRUS_LOGO_STYLE, 0,
                    UserHandle.USER_CURRENT);
            mCitrusLogoStyle.setValue(String.valueOf(citrusLogoStyle));
            mCitrusLogoStyle.setSummary(mCitrusLogoStyle.getEntry());
            mCitrusLogoStyle.setOnPreferenceChangeListener(this);
    }

	@Override
    public boolean onPreferenceChange(Preference preference, Object newValue) {
        if (preference == mCitrusLogoColor) {
            String hex = ColorPickerPreference.convertToARGB(
                    Integer.valueOf(String.valueOf(newValue)));
            preference.setSummary(hex);
            int intHex = ColorPickerPreference.convertToColorInt(hex);
            Settings.System.putInt(getContentResolver(),
                    Settings.System.STATUS_BAR_CITRUS_LOGO_COLOR, intHex);
            return true;
        } else if (preference == mCitrusLogoStyle) {
                int citrusLogoStyle = Integer.valueOf((String) newValue);
                int index = mCitrusLogoStyle.findIndexOfValue((String) newValue);
                Settings.System.putIntForUser(
                        getContentResolver(), Settings.System.STATUS_BAR_CITRUS_LOGO_STYLE, citrusLogoStyle,
                        UserHandle.USER_CURRENT);
                mCitrusLogoStyle.setSummary(
                        mCitrusLogoStyle.getEntries()[index]);
                return true;
        }
        return false;
    }


    @Override
    protected int getMetricsCategory() {
        return MetricsEvent.CUSTOM_SQUASH;
    }

    @Override
    public void onResume() {
        super.onResume();
    }

}