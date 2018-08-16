package sheva.singapp.mvp.ui.interfaces;

import java.util.List;

import sheva.singapp.mvp.model.entities.Exercise;

/**
 * Created by shevc on 23.07.2017.
 * Let's GO!
 */

public interface ISingSongActivityView extends IView{
    void onError(String message, String title, String positiveButtonText, String cancelButtonText);
}
