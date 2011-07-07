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

import static org.zakky.usbdevicelist.UsbConstantsUtil.getClassString;
import static org.zakky.usbdevicelist.UsbConstantsUtil.getIdString;
import static org.zakky.usbdevicelist.UsbConstantsUtil.getPidString;
import static org.zakky.usbdevicelist.UsbConstantsUtil.getProtocolString;
import static org.zakky.usbdevicelist.UsbConstantsUtil.getSubclassString;
import static org.zakky.usbdevicelist.UsbConstantsUtil.getVidString;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbInterface;
import android.hardware.usb.UsbManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.ExpandableListView.OnChildClickListener;
import android.widget.SimpleExpandableListAdapter;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

/**
 * USB のデバイスリスト(とそのデバイスが持っているインタフェースのリスト)を表示する
 * フラグメントです。
 */
public class UsbDeviceListFragment extends Fragment {

    /**
     * リスト上の interface が選択された際に呼ばれるコールバックのためのインタフェースです。
     */
    public interface OnUsbInterfaceSelectionListener {
        /**
         * インタフェースが選択された/選択解除された場合に呼ばれるインタフェース。
         * 
         * @param iface 選択された場合は、対応する {@link UsbInterface} オブジェクト、
         * 選択解除された場合は {@code null} が渡されます。
         */
        public void onUsbInterfaceSelected(UsbInterface iface);
    }

    private static final class DeviceMapKeys {
        private static final String NAME = "name";

        private static final String ID = "id";

        private static final String CLASS = "class";

        private static final String VENDOR_ID = "vid";

        private static final String PRODUCT_ID = "pid";
    }

    private static final class InterfaceMapKeys {
        private static final String ID = "id";

        private static final String CLASS = "class";

        private static final String SUBCLASS = "subclass";

        private static final String PROTOCOL = "protocol";

        private static final String INTERFACE = "interface";
    }

    /*
     * メニュー識別用の定数群
     */

    /**
     * デバイスリストの更新メニューのための定数
     */
    private static final int MENU_REFRESH_ID = Menu.FIRST;

    private UsbManager mUsbManager;

    /**
     * リスト上でインタフェースが選択された場合の Callback を保持するメンバ変数。
     */
    private OnUsbInterfaceSelectionListener mInterfaceSelectionCallback;

    private ExpandableListView mExpList;

    public UsbDeviceListFragment() {
        // nothing to do
        assert true;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        mUsbManager = (UsbManager) getActivity().getSystemService(Context.USB_SERVICE);

        mInterfaceSelectionCallback = (OnUsbInterfaceSelectionListener) activity;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View v = inflater.inflate(R.layout.device_list, container);

        mExpList = (ExpandableListView) v.findViewById(R.id.exp_list);
        mExpList.setChoiceMode(ExpandableListView.CHOICE_MODE_SINGLE);
        return v;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

        mUsbManager = null;
        mInterfaceSelectionCallback = null;
    }

    @Override
    public void onStart() {
        super.onStart();

        updateDeviceList();
        mInterfaceSelectionCallback.onUsbInterfaceSelected(null);
        mExpList.setOnChildClickListener(mInterfaceClickListener);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);

        MenuItem add = menu.add(0, MENU_REFRESH_ID, 0, R.string.menu_refresh);
        add.setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM | MenuItem.SHOW_AS_ACTION_WITH_TEXT);
        add.setIcon(R.drawable.ic_menu_refresh);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case MENU_REFRESH_ID:
                updateDeviceList();
                mInterfaceSelectionCallback.onUsbInterfaceSelected(null);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void updateDeviceList() {
        final Map<String, UsbDevice> devices = mUsbManager.getDeviceList();

        final SimpleExpandableListAdapter adapter = createAdapter(devices);
        mExpList.setAdapter(adapter);

        final int groupCount = adapter.getGroupCount();
        for (int i = 0; i < groupCount; i++) {
            mExpList.expandGroup(i);
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        mExpList.setAdapter((ExpandableListAdapter) null);
        mExpList.setOnChildClickListener(null);
    }

    private final OnChildClickListener mInterfaceClickListener = new OnChildClickListener() {
        @Override
        public boolean onChildClick(ExpandableListView parent, View v, int groupPosition,
                int childPosition, long id) {
            final ExpandableListAdapter adapter = parent.getExpandableListAdapter();

            @SuppressWarnings("unchecked")
            final Map<String, Object> m = (Map<String, Object>) adapter.getChild(groupPosition,
                    childPosition);
            if (m == null) {
                return false;
            }

            final UsbInterface iface = (UsbInterface) m.get(InterfaceMapKeys.INTERFACE);
            if (iface == null) {
                return false;
            }

            mInterfaceSelectionCallback.onUsbInterfaceSelected(iface);
            return true;
        }
    };

    /**
     * {@link UsbDevice} を、名前でソートするためのコンパレータ。
     */
    private static final Comparator<Map<String, ?>> DEV_COMPARATOR = new Comparator<Map<String, ?>>() {
        @Override
        public int compare(Map<String, ?> object1, Map<String, ?> object2) {
            final String name1 = (String) object1.get(DeviceMapKeys.NAME);
            final String name2 = (String) object2.get(DeviceMapKeys.NAME);
            return name1.compareTo(name2);
        }
    };

    /**
     * {@link UsbInterface} を Id でソートするためのコンパレータ。
     */
    private static final Comparator<Map<String, ?>> INTERFACE_COMPARATOR = new Comparator<Map<String, ?>>() {
        @Override
        public int compare(Map<String, ?> object1, Map<String, ?> object2) {
            final UsbInterface iface1 = (UsbInterface) object1.get(InterfaceMapKeys.INTERFACE);
            final UsbInterface iface2 = (UsbInterface) object2.get(InterfaceMapKeys.INTERFACE);
            return Integer.valueOf(iface1.getId()).compareTo(Integer.valueOf(iface2.getId()));
        }
    };

    private SimpleExpandableListAdapter createAdapter(final Map<String, UsbDevice> devices) {
        final List<Map<String, ?>> deviceEntries = Lists.newArrayList();
        final List<List<Map<String, ?>>> ifaceEntriesList = Lists.newArrayList();

        for (Entry<String, UsbDevice> entry : devices.entrySet()) {
            final UsbDevice dev = entry.getValue();

            final Map<String, String> deviceEntry = Maps.newHashMap();
            deviceEntries.add(deviceEntry);
            deviceEntry.put(DeviceMapKeys.NAME, dev.getDeviceName());
            deviceEntry.put(DeviceMapKeys.ID, "Id: " + getIdString(dev));
            deviceEntry.put(DeviceMapKeys.CLASS, "Class: " + getClassString(dev));
            deviceEntry.put(DeviceMapKeys.VENDOR_ID, "VendorId: " + getVidString(dev));
            deviceEntry.put(DeviceMapKeys.PRODUCT_ID, "ProductId: " + getPidString(dev));

            // build list for interfaces
            final int ifaceCount = dev.getInterfaceCount();
            final List<Map<String, ?>> ifaceEntries = Lists.newArrayListWithCapacity(ifaceCount);
            ifaceEntriesList.add(ifaceEntries);
            for (int i = 0; i < ifaceCount; i++) {
                final UsbInterface iface = dev.getInterface(i);

                final Map<String, Object> ifaceEntry = Maps.newHashMap();
                ifaceEntries.add(ifaceEntry);

                ifaceEntry.put(InterfaceMapKeys.ID, "Id: " + getIdString(iface));
                ifaceEntry.put(InterfaceMapKeys.CLASS, "Class: " + getClassString(iface));
                ifaceEntry.put(InterfaceMapKeys.SUBCLASS, "Subclass: " + getSubclassString(iface));
                ifaceEntry.put(InterfaceMapKeys.PROTOCOL, "Protocol: " + getProtocolString(iface));

                // ソートや詳細画面呼び出しに UsbInterface が必要なのでMapにいれておく
                ifaceEntry.put(InterfaceMapKeys.INTERFACE, iface);
            }
            Collections.sort(ifaceEntries, INTERFACE_COMPARATOR);
        }
        Collections.sort(deviceEntries, DEV_COMPARATOR);

        final SimpleExpandableListAdapter adapter;
        adapter = new SimpleExpandableListAdapter(getActivity(), deviceEntries,
                R.layout.device_list_device_row, new String[] {
                        DeviceMapKeys.NAME, //
                        DeviceMapKeys.ID, //
                        DeviceMapKeys.CLASS, //
                        DeviceMapKeys.VENDOR_ID, //
                        DeviceMapKeys.PRODUCT_ID
                }, //
                new int[] {
                        R.id.dev_name, //
                        R.id.dev_id, //
                        R.id.dev_class, //
                        R.id.dev_vendor_id, //
                        R.id.dev_product_id
                }, //
                ifaceEntriesList, R.layout.device_list_interface_row, new String[] {
                        InterfaceMapKeys.ID, //
                        InterfaceMapKeys.CLASS, //
                        InterfaceMapKeys.SUBCLASS, //
                        InterfaceMapKeys.PROTOCOL
                }, //
                new int[] {
                        R.id.iface_id, //
                        R.id.iface_class, //
                        R.id.iface_subclass, //
                        R.id.iface_protocol
                });
        return adapter;
    }

}
