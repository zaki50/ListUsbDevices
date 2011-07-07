/*
 * Copyright 2011 YAMAZAKI Makoto<makoto1975@gmail.com>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.zakky.usbdevicelist;

import org.zakky.usbdevicelist.UsbDeviceListFragment.OnUsbInterfaceSelectionListener;

import android.app.Activity;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.hardware.usb.UsbInterface;
import android.os.Bundle;

public class InfoActivity extends Activity implements OnUsbInterfaceSelectionListener {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.info);
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    private static final String TAG_DETAIL = "detail";

    @Override
    public void onUsbInterfaceSelected(UsbInterface iface) {
        final FragmentManager fm = getFragmentManager();
        final UsbDeviceDetailFragment prevDetail = (UsbDeviceDetailFragment) fm.findFragmentByTag(TAG_DETAIL);

        final FragmentTransaction tr = fm.beginTransaction();
        if (iface == null) {
            if (prevDetail != null) {
                tr.remove(prevDetail);
            }
        } else if (prevDetail == null || !iface.equals(prevDetail.getTarget())) {
            final UsbDeviceDetailFragment newDetail = UsbDeviceDetailFragment.newInstance(iface);
            tr.replace(R.id.device_detail_container, newDetail, TAG_DETAIL);
            tr.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
        }
        tr.commit();
    }

}
