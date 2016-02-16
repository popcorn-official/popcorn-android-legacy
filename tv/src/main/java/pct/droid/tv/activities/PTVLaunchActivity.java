/*
 * This file is part of Popcorn Time.
 *
 * Popcorn Time is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Popcorn Time is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Popcorn Time. If not, see <http://www.gnu.org/licenses/>.
 */

package pct.droid.tv.activities;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;

import pct.droid.base.content.preferences.Prefs;
import pct.droid.base.torrent.StreamInfo;
import pct.droid.base.utils.PrefUtils;
import pct.droid.tv.service.RecommendationService;

public class PTVLaunchActivity extends Activity {

	private static final int PERMISSIONS_REQUEST = 1232;

	@Override
    protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		Intent recommendationIntent = new Intent(this, RecommendationService.class);
		startService(recommendationIntent);

		if (ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
			ActivityCompat.requestPermissions(this, new String[]{ Manifest.permission.RECORD_AUDIO, Manifest.permission.WRITE_EXTERNAL_STORAGE }, PERMISSIONS_REQUEST);
            return;
		}

        proceedCreate();
	}

    private void proceedCreate() {
        Boolean firstRun = PrefUtils.get(this, Prefs.FIRST_RUN, true);

        if (firstRun) {
            //run the welcome wizard
            PTVWelcomeActivity.startActivity(this);
        } else {
            String action = getIntent().getAction();
            Uri data = getIntent().getData();
            if (action != null && action.equals(Intent.ACTION_VIEW) && data != null) {
                String streamUrl = data.toString();
                try {
                    streamUrl = URLDecoder.decode(streamUrl, "utf-8");
                    PTVStreamLoadingActivity.startActivity(this, new StreamInfo(streamUrl));
                    finish();
                    return;
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
            }
            PTVMainActivity.startActivity(this);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[], @NonNull int[] grantResults) {
        switch (requestCode) {
            case PERMISSIONS_REQUEST: {
                if (grantResults.length == 2 && grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                    proceedCreate();
                } else {
                    finish();
                }
            }
        }
    }
}
