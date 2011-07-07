
package org.zakky.usbdevicelist;

import android.hardware.usb.UsbConstants;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbEndpoint;
import android.hardware.usb.UsbInterface;

public final class UsbConstantsUtil {

    public static String getIdString(UsbDevice dev) {
        final int id = dev.getDeviceId();
        return "" + id;
    }

    public static String getClassString(UsbDevice dev) {
        // see http://www.usb.org/developers/defined_class
        final String name;
        switch (dev.getDeviceClass()) {
            case UsbConstants.USB_CLASS_PER_INTERFACE:
                name = "Periferal";
                break;
            case UsbConstants.USB_CLASS_AUDIO:
                name = "Audio";
                break;
            case UsbConstants.USB_CLASS_COMM:
                name = "Comm";
                break;
            case UsbConstants.USB_CLASS_HID:
                name = "HID";
                break;
            case UsbConstants.USB_CLASS_PHYSICA:
                name = "PID";
                break;
            case UsbConstants.USB_CLASS_STILL_IMAGE:
                name = "Image";
                break;
            case UsbConstants.USB_CLASS_PRINTER:
                name = "Printer";
                break;
            case UsbConstants.USB_CLASS_MASS_STORAGE:
                name = "MassStorage";
                break;
            case UsbConstants.USB_CLASS_HUB:
                name = "Hub";
                break;
            case UsbConstants.USB_CLASS_CDC_DATA:
                name = "CDC-Data";
                break;
            case UsbConstants.USB_CLASS_CSCID:
                name = "SmartCard";
                break;
            case UsbConstants.USB_CLASS_CONTENT_SEC:
                name = "ContentSec";
                break;
            case UsbConstants.USB_CLASS_VIDEO:
                name = "VideoCamera";
                break;
            case 0x0F:
                name = "PersonalHealthcare";
                break;
            case 0xDC:
                name = "DiagnosticDevice";
                break;
            case UsbConstants.USB_CLASS_WIRELESS_CONTROLLER:
                name = "Wireless";
                break;
            case UsbConstants.USB_CLASS_MISC:
                name = "Misc";
                break;
            case UsbConstants.USB_CLASS_APP_SPEC:
                name = "AppSpec";
                break;
            case UsbConstants.USB_CLASS_VENDOR_SPEC:
                name = "VendorSpec";
                break;
            default:
                name = "Unknown";
                break;
        }
        return name + "(" + to2HexString(dev.getDeviceClass()) + ", "
                + to2HexString(dev.getDeviceSubclass()) + ", "
                + to2HexString(dev.getDeviceProtocol()) + ")";
    }

    public static String getVidString(UsbDevice dev) {
        final int vid = dev.getVendorId();
        return to4HexString(vid);
    }

    public static String getPidString(UsbDevice dev) {
        final int pid = dev.getProductId();
        return to4HexString(pid);
    }

    /*
     * UsbInterface の値を文字列化するためのメソッド群
     */
    
    
    public static String getIdString(UsbInterface iface) {
        final int id = iface.getId();
        return "" + id;
    }

    public static String getClassString(UsbInterface iface) {
        final int clazz = iface.getInterfaceClass();
        return "" + clazz;
    }

    public static String getSubclassString(UsbInterface iface) {
        final int subclass = iface.getInterfaceSubclass();
        return "" + subclass;
    }

    public static String getProtocolString(UsbInterface iface) {
        final int protocol = iface.getInterfaceProtocol();
        return "" + protocol;
    }

    public static String getAddressString(UsbEndpoint ep) {
        final int address = ep.getAddress();
        return "" + address;
    }

    public static String getNumberString(UsbEndpoint ep) {
        final int number = ep.getEndpointNumber();
        return "" + number;
    }

    public static String getAttributesString(UsbEndpoint ep) {
        final int attrs = ep.getAttributes();
        return "" + attrs;
    }

    public static String getDirectionString(UsbEndpoint ep) {
        final int d = ep.getDirection();
        return (d == UsbConstants.USB_DIR_IN) ? "IN" : "OUT";
    }

    public static String getIntervalString(UsbEndpoint ep) {
        final int interval = ep.getInterval();
        return "" + interval;
    }

    public static String getMaxPacketSizeString(UsbEndpoint ep) {
        final int size = ep.getMaxPacketSize();
        return "" + size;
    }

    public static String getTypeString(UsbEndpoint ep) {
        switch (ep.getType()) {
            case UsbConstants.USB_ENDPOINT_XFER_CONTROL:
                return "Control";
            case UsbConstants.USB_ENDPOINT_XFER_INT:
                return "Interrupt";
            case UsbConstants.USB_ENDPOINT_XFER_BULK:
                return "Bulk";
            case UsbConstants.USB_ENDPOINT_XFER_ISOC:
                return "Isochronous";
            default:
                return "Unknown";
        }
    }

    private static final String to2HexString(int value) {
        if (value < 0 || 0xff < value) {
            return "invalid";
        }
        final String hex = Integer.toHexString(value);
        switch (hex.length()) {
            case 2:
                return "0x" + hex;
            case 1:
                return "0x0" + hex;
            default:
                return "invalid";
        }
    }

    private static final String to4HexString(int value) {
        if (value < 0 || 0xffff < value) {
            return "invalid";
        }
        final String hex = Integer.toHexString(value);
        switch (hex.length()) {
            case 4:
                return "0x" + hex;
            case 3:
                return "0x0" + hex;
            case 2:
                return "0x00" + hex;
            case 1:
                return "0x000" + hex;
            default:
                return "invalid";
        }
    }
}
