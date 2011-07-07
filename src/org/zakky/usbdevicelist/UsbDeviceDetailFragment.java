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

import static org.zakky.usbdevicelist.UsbConstantsUtil.getAddressString;
import static org.zakky.usbdevicelist.UsbConstantsUtil.getAttributesString;
import static org.zakky.usbdevicelist.UsbConstantsUtil.getDirectionString;
import static org.zakky.usbdevicelist.UsbConstantsUtil.getIntervalString;
import static org.zakky.usbdevicelist.UsbConstantsUtil.getMaxPacketSizeString;
import static org.zakky.usbdevicelist.UsbConstantsUtil.getNumberString;
import static org.zakky.usbdevicelist.UsbConstantsUtil.getTypeString;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import android.app.Activity;
import android.app.ListFragment;
import android.hardware.usb.UsbEndpoint;
import android.hardware.usb.UsbInterface;
import android.os.Bundle;
import android.os.Parcelable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SimpleAdapter;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

public class UsbDeviceDetailFragment extends ListFragment {

    private static final String ARG_TARGET_INTERFACE = "interface";

    public static UsbDeviceDetailFragment newInstance(UsbInterface targetInterface) {
        final UsbDeviceDetailFragment f = new UsbDeviceDetailFragment();

        final Bundle args = new Bundle();
        args.putParcelable(ARG_TARGET_INTERFACE, targetInterface);
        f.setArguments(args);

        return f;
    }

    public UsbDeviceDetailFragment() {
        // nothing to do
        assert true;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        final UsbInterface target = getTarget();
        if (target == null) {
            clearEndpoints();
        } else {
            showEndpoints(target);
        }
    }

    public UsbInterface getTarget() {
        final Parcelable p = getArguments().getParcelable(ARG_TARGET_INTERFACE);
        if (!(p instanceof UsbInterface)) {
            return null;
        }
        return (UsbInterface) p;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View v = inflater.inflate(R.layout.device_detail, container, false);
        return v;
    }

    private static final class EndMapointpKeys {

        private static final String ADDRESS = "address";

        private static final String NUMBER = "number";

        private static final String ATTRS = "attributes";

        private static final String DIR = "direction";

        private static final String INTERVAL = "interval";

        private static final String MAX_PACKET_SIZE = "max_pkt";

        private static final String TYPE = "type";

        private static final String ENDPOINT = "endpoint";
    }

    private static final class EndpointComparator implements Comparator<Map<String, Object>> {
        @Override
        public int compare(Map<String, Object> object1, Map<String, Object> object2) {
            final UsbEndpoint ep1 = (UsbEndpoint) object1.get(EndMapointpKeys.ENDPOINT);
            final UsbEndpoint ep2 = (UsbEndpoint) object2.get(EndMapointpKeys.ENDPOINT);
            return Integer.valueOf(ep1.getEndpointNumber()).compareTo(
                    Integer.valueOf(ep2.getEndpointNumber()));
        }
    }

    private static final EndpointComparator EP_COMPARATOR = new EndpointComparator();

    private void showEndpoints(UsbInterface iface) {
        if (!isAdded()) {
            return;
        }

        final int endpointCount = iface.getEndpointCount();
        final List<Map<String, Object>> epEntries = Lists.newArrayListWithCapacity(endpointCount);
        for (int i = 0; i < endpointCount; i++) {
            final Map<String, Object> epEntry = Maps.newHashMap();
            final UsbEndpoint endpoint = iface.getEndpoint(i);
            epEntry.put(EndMapointpKeys.ADDRESS, "Address: " + getAddressString(endpoint));
            epEntry.put(EndMapointpKeys.NUMBER, "Endpoint Number: " + getNumberString(endpoint));
            epEntry.put(EndMapointpKeys.ATTRS, "Attributes: " + getAttributesString(endpoint));
            epEntry.put(EndMapointpKeys.DIR, "Direction: " + getDirectionString(endpoint));
            epEntry.put(EndMapointpKeys.INTERVAL, "Interval: " + getIntervalString(endpoint));
            epEntry.put(EndMapointpKeys.MAX_PACKET_SIZE, "MaxPacketSize: "
                    + getMaxPacketSizeString(endpoint));
            epEntry.put(EndMapointpKeys.TYPE, "Type: " + getTypeString(endpoint));
            epEntry.put(EndMapointpKeys.ENDPOINT, endpoint);

            epEntries.add(epEntry);
        }
        Collections.sort(epEntries, EP_COMPARATOR);

        final Activity act = getActivity();
        final SimpleAdapter adapter = new SimpleAdapter(act, epEntries, R.layout.device_detail_row,
                new String[] {
                        EndMapointpKeys.ADDRESS, //
                        EndMapointpKeys.NUMBER, //
                        EndMapointpKeys.ATTRS, //
                        EndMapointpKeys.DIR, //
                        EndMapointpKeys.INTERVAL, //
                        EndMapointpKeys.MAX_PACKET_SIZE, //
                        EndMapointpKeys.TYPE
                }, //
                new int[] {
                        R.id.ep_address, //
                        R.id.ep_number, //
                        R.id.ep_attrs, //
                        R.id.ep_dir, //
                        R.id.ep_interval, //
                        R.id.ep_max_packet_size, //
                        R.id.ep_type
                });
        setListAdapter(adapter);
    }

    private void clearEndpoints() {
        setListAdapter(null);
    }
}
