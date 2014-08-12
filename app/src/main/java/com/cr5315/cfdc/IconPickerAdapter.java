/*
 * Countdown for DashClock Extension
 * Copyright (C) 2014 Benjamin Butzow
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 3 of the License, or
 * (at your option) any later version.
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301  USA
 */

package com.cr5315.cfdc;

import android.content.Context;
import android.graphics.Color;
import android.graphics.PorterDuff.Mode;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;

public class IconPickerAdapter extends BaseAdapter {
    private Context context;

    public IconPickerAdapter(Context context) {
        this.context = context;
    }

    public int getCount() {
        return thumbs.length;
    }

    public Object getItem(int position) {
        return position;
    }

    public long getItemId(int position) {
        return thumbs[position];
    }

    // create a new ImageView for each item referenced by the Adapter
    public View getView(int position, View convertView, ViewGroup parent) {
        ImageView imageView;
        if (convertView == null) {  // if it's not recycled, initialize some attributes
            imageView = new ImageView(context);
            imageView.setLayoutParams(new GridView.LayoutParams(85, 85));
            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
            imageView.setPadding(8, 8, 8, 8);
        } else {
            imageView = (ImageView) convertView;
        }
        int color = Color.parseColor("#FF000000");
        Mode mode = Mode.SRC_ATOP;
        Drawable draw = context.getResources().getDrawable(thumbs[position]);
        draw.setColorFilter(color, mode);

        imageView.setImageDrawable(draw);
        return imageView;
    }

    // references to our images
    public static Integer[] thumbs = {
            R.drawable.device_access_time, R.drawable.action_about, R.drawable.action_help,
            R.drawable.action_search, R.drawable.action_settings, R.drawable.alerts_and_states_airplane_mode_off,
            R.drawable.alerts_and_states_airplane_mode_on, R.drawable.alerts_and_states_error, R.drawable.alerts_and_states_warning,
            // av
            R.drawable.av_add_to_queue, R.drawable.av_download, R.drawable.av_fast_forward,
            R.drawable.av_full_screen, R.drawable.av_make_available_offline, R.drawable.av_next,
            R.drawable.av_pause, R.drawable.av_pause_over_video, R.drawable.av_play,
            R.drawable.av_play_over_video, R.drawable.av_previous, R.drawable.av_repeat,
            R.drawable.av_replay, R.drawable.av_return_from_full_screen, R.drawable.av_rewind,
            R.drawable.av_shuffle, R.drawable.av_stop, R.drawable.av_upload,
            // collections
            R.drawable.collections_cloud, R.drawable.collections_collection, R.drawable.collections_go_to_today,
            R.drawable.collections_labels, R.drawable.collections_new_label, R.drawable.collections_sort_by_size,
            R.drawable.collections_view_as_grid, R.drawable.collections_view_as_list, R.drawable.content_attachment,
            // content
            R.drawable.content_backspace, R.drawable.content_copy, R.drawable.content_copy,
            R.drawable.content_cut, R.drawable.content_discard, R.drawable.content_edit,
            R.drawable.content_email, R.drawable.content_import_export, R.drawable.content_merge,
            R.drawable.content_new, R.drawable.content_new_attachment, R.drawable.content_new_email,
            R.drawable.content_new_event, R.drawable.content_new_picture, R.drawable.content_paste,
            R.drawable.content_picture, R.drawable.content_read, R.drawable.content_remove,
            R.drawable.content_save, R.drawable.content_select_all, R.drawable.content_split,
            R.drawable.content_undo, R.drawable.content_unread, R.drawable.device_access_accounts,
            // device
            R.drawable.device_access_add_alarm, R.drawable.device_access_alarms, R.drawable.device_access_battery,
            R.drawable.device_access_bluetooth, R.drawable.device_access_bluetooth_connected, R.drawable.device_access_bluetooth_searching,
            R.drawable.device_access_brightness_auto, R.drawable.device_access_brightness_high, R.drawable.device_access_brightness_medium,
            R.drawable.device_access_brightness_low, R.drawable.device_access_call, R.drawable.device_access_camera,
            R.drawable.device_access_data_usage, R.drawable.device_access_dial_pad, R.drawable.device_access_end_call,
            R.drawable.device_access_flash_automatic, R.drawable.device_access_flash_off, R.drawable.device_access_flash_on,
            R.drawable.device_access_location_found, R.drawable.device_access_location_off, R.drawable.device_access_location_searching,
            R.drawable.device_access_mic, R.drawable.device_access_mic_muted, R.drawable.device_access_network_cell,
            R.drawable.device_access_network_wifi, R.drawable.device_access_new_account, R.drawable.device_access_not_secure,
            R.drawable.device_access_ring_volume, R.drawable.device_access_screen_locked_to_landscape, R.drawable.device_access_screen_locked_to_portrait,
            R.drawable.device_access_screen_rotation, R.drawable.device_access_sd_storage, R.drawable.device_access_secure,
            R.drawable.device_access_storage, R.drawable.device_access_switch_camera, R.drawable.device_access_switch_video,
            R.drawable.device_access_usb, R.drawable.device_access_video, R.drawable.device_access_volume_muted,
            R.drawable.device_access_volume_on, R.drawable.hardware_computer, R.drawable.hardware_dock,
            // hardware
            // FOR THE USER
            R.drawable.hardware_gamepad, R.drawable.hardware_headphones, R.drawable.hardware_headset,
            R.drawable.hardware_keyboard, R.drawable.hardware_mouse, R.drawable.hardware_phone,
            // images and the rest
            R.drawable.images_crop, R.drawable.images_rotate_left, R.drawable.images_rotate_right,
            R.drawable.images_slideshow, R.drawable.location_directions, R.drawable.location_map,
            R.drawable.location_place, R.drawable.location_web_site, R.drawable.navigation_accept,
            R.drawable.navigation_back, R.drawable.navigation_cancel, R.drawable.navigation_collapse,
            R.drawable.navigation_expand, R.drawable.navigation_forward, R.drawable.navigation_next_item,
            R.drawable.navigation_previous_item, R.drawable.navigation_refresh, R.drawable.rating_bad,
            R.drawable.rating_favorite, R.drawable.rating_good, R.drawable.rating_half_important,
            R.drawable.rating_important, R.drawable.rating_not_important, R.drawable.social_add_group,
            R.drawable.social_add_person, R.drawable.social_cc_bcc, R.drawable.social_chat,
            R.drawable.social_forward, R.drawable.social_group, R.drawable.social_person,
            R.drawable.social_reply, R.drawable.social_reply_all, R.drawable.social_send_now,
            R.drawable.social_share
    };
}