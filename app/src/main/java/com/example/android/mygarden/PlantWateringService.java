package com.example.android.mygarden;

import android.app.IntentService;
import android.content.ContentValues;
import android.content.Intent;
import android.content.Context;
import android.net.Uri;
import android.util.Log;
import android.widget.Toast;

import com.example.android.mygarden.provider.PlantContract;
import com.example.android.mygarden.utils.PlantUtils;

import static com.example.android.mygarden.provider.PlantContract.BASE_CONTENT_URI;
import static com.example.android.mygarden.provider.PlantContract.PATH_PLANTS;

// TODO (2): Create a plant watering service that extends IntentService and supports the
// action ACTION_WATER_PLANTS which updates last_watered timestamp for all plants still alive

public class PlantWateringService extends IntentService {

    /**
     * To keep things organized, it’s best to define the actions that the IntentService
     * can handle, we will start by defining our first action as ACTION_WATER_PLANTS
     */
    public static final String ACTION_WATER_PLANTS =
            "com.example.android.mygarden.action.water_plants";

    public PlantWateringService() {
        super("PlantWateringService");
    }

    /**
     * Next we will create a static method called startActionWaterPlants that allows
     * explicitly triggering the Service to perform this action, inside simply create
     * an intent that refers to the same class and set the action to ACTION_WATER_PLANTS
     * and call start service
     *
     * @see IntentService
     */
    public static void startActionWaterPlants(Context context) {
        Intent intent = new Intent(context, PlantWateringService.class);
        intent.setAction(ACTION_WATER_PLANTS);
        context.startService(intent);
    }

    /**
     * To handle this action we need to override onHandleIntent, where
     * you can extract the action and handle each action type separately.
     *
     * @param intent
     */
    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            final String action = intent.getAction();
            if (ACTION_WATER_PLANTS.equals(action)) {
                handleActionWaterPlants();
            }
        }
    }

    /*
    Then finally we implement the handleActionWaterPlants method.
    To water all plants we just run an update query setting the last watered time to now,
    but only for those plants that are still alive. To check if a plant is still alive,
    you can compare the last watered time with the time now and if the difference is larger
    than MAX_AGE_WITHOUT_WATER, then the plant is dead!
     */
    private void handleActionWaterPlants() {
        Log.d(PlantWateringService.class.getSimpleName(), "handleActionWaterPlants called");
        Uri PLANTS_URI = BASE_CONTENT_URI.buildUpon().appendPath(PATH_PLANTS).build();
        ContentValues contentValues = new ContentValues();
        long timeNow = System.currentTimeMillis();
        contentValues.put(PlantContract.PlantEntry.COLUMN_LAST_WATERED_TIME, timeNow);
        // Update only plants that are still alive
        getContentResolver().update(
                PLANTS_URI,
                contentValues,
                PlantContract.PlantEntry.COLUMN_LAST_WATERED_TIME+">?",
                new String[]{String.valueOf(timeNow - PlantUtils.MAX_AGE_WITHOUT_WATER)});
    }

}
