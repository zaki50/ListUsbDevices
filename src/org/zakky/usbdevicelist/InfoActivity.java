
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
