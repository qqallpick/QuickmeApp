package com.zsuuu.quickmeapp.FilePicker;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.content.res.AppCompatResources;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.zsuuu.quickmeapp.MainActivity;
import com.zsuuu.quickmeapp.R;
import com.zsuuu.quickmeapp.SettingsHelper;

 

public class ListFilePicker extends FilePicker {
     
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_file_picker, container, false);
        fileListView = (ListView) view.findViewById(R.id.fileListView);
        initializeFilePickerWithViewAndFileListAdapterGetViewCallback(view, fileListAdapterGetViewCallback);
        return view;
    }

         private FileListAdapterGetViewCallback fileListAdapterGetViewCallback = new FileListAdapterGetViewCallback() {
        @Override
        public View getView(int position, View convertView, ViewGroup parent, FilePicker.FileListAdapter fileListAdapter) {
            if (convertView == null) {
                convertView = LayoutInflater.from(getContext()).inflate(R.layout.listitem_listfileviewer_item, parent, false);
            }
            Pair<String, Boolean> file = fileListAdapter.getItem(position);
            ImageView listItemImageView = (ImageView) convertView.findViewById(R.id.listItemImageView);
            TextView listItemTextView = (TextView) convertView.findViewById(R.id.listItemTextView);
            if (file.second) {
                listItemImageView.setImageDrawable(AppCompatResources.getDrawable(getContext(), R.drawable.ic_folder));
            } else {
                listItemImageView.setImageDrawable(AppCompatResources.getDrawable(getContext(), R.drawable.ic_file));
            }
            listItemTextView.setText(file.first);

             
            if (SettingsHelper.getUseDarkTeme(getContext())) {
                listItemTextView.setTextColor(((MainActivity)getActivity()).getDarkThemeColor(android.R.attr.textColorPrimary));
            }
            return convertView;
        }
    };
}
