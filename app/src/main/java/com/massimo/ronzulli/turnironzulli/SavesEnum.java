package com.massimo.ronzulli.turnironzulli;

import android.graphics.Color;

public class SavesEnum {
    public AppSettings appSettings;
    public class AppSettings{
        public String APP_SETTINGS;
        public String GENERAL_SETTINGS;
        public String STYLE_SETTINGS;
        public String NOTE_SETTINGS;
        public String ROTATION_SETTINGS;
        public String LEAVE_SETTINGS;
        public String FIRST_TIME_OPEN_STYLES_ACTIVITY;
        public String FIRST_TIME_OPEN_MAKE_SHIF_ACTIVITY;
        public String TOTAL_STYLES;//totale stili attivi
        public String SELECTED_STYLE;
        public String LAST_ID_STYLE;//ultimo id style memorizzato
        public String DEFAULT_SOUND;
        public String DEFAULT_CELL_COLOR_STR;
        public String DEFAULT_BORDER_SIZE_STR;
        public String DEFAULT_CHAR_SIZE_STR;
        public int DEFAULT_CELL_COLOR;
        public int DEFAULT_BORDER_SIZE;
        public float DEFAULT_CHAR_SIZE;
        AppSettings (){
            APP_SETTINGS ="appSettings";
            GENERAL_SETTINGS="general_settings";
            STYLE_SETTINGS="styles";
            NOTE_SETTINGS="notes";
            ROTATION_SETTINGS="rotations";
            LEAVE_SETTINGS="leave";
            FIRST_TIME_OPEN_STYLES_ACTIVITY="{firstTimeOpenStylesActivity}";
            FIRST_TIME_OPEN_MAKE_SHIF_ACTIVITY="{firstTimeOpenMakeShiftActivity}";
            SELECTED_STYLE="{selected style}";
            TOTAL_STYLES="{total styles}";
            LAST_ID_STYLE="{last id style}";
            DEFAULT_SOUND="{default sound}";
            DEFAULT_CELL_COLOR_STR="{defaultCellColor}";
            DEFAULT_BORDER_SIZE_STR="{defaultBorderSize}";
            DEFAULT_CHAR_SIZE_STR="{defaultCharSize}";
            DEFAULT_CELL_COLOR= Color.parseColor("#F7F7ED");
            DEFAULT_BORDER_SIZE=6;
            DEFAULT_CHAR_SIZE=12;
        }
    }
    public SavesEnum() {
        appSettings=new AppSettings();
    }

}
