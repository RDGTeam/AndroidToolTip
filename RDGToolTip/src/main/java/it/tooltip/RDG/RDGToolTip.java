package it.tooltip.RDG;

import android.app.Activity;
import android.content.Context;
import android.view.View;

import it.tooltip.closeManager.ClosePolicy;
import it.tooltip.dialog.CustomToolTip;
import it.tooltip.position.ToolTipPositionManager;


/**
 * Created on 31/01/2017.
 */

public class RDGToolTip {

    //context
    private Context context;

    //fragment dialog instance
    private CustomToolTip tooltip;

    //tooltip position
    private ToolTipPositionManager position;
    private View anchorView;

    //messages of the tooltip
    private String message;
    private String title;

    // cancelable property
    private boolean isCancelable;


    public RDGToolTip(Context context) {
        this.context = context;
        tooltip = new CustomToolTip();
        tooltip.createToolTip(context);
    }

    /*************************************************************************************************************
     * ************************************************  Methods  ************************************************
     *************************************************************************************************************/

    /**
     * The close policy : default is CLOSE_OUTSIDE
     *
     * @param closePolicy
     */
    public void setClosePolicy(ClosePolicy closePolicy) {
        tooltip.setClosePolicy(closePolicy);
    }

    /**
     * Method to show the tooltip
     */
    public void show() {
        Activity activity = (Activity) context;
        tooltip.setToolTipPosition(anchorView, position);
        tooltip.show(activity.getFragmentManager(), "RDGToolTipManager");
    }

    /**
     * Set the position of the tooltip as a function of the the gravity on given view
     *
     * @param anchorView
     * @param position
     */
    public void setTooltipPosition(View anchorView, ToolTipPositionManager position) {
        this.position = position;
        this.anchorView = anchorView;
    }

    /*****************************************************************************************************************
     * *************************************************  Set Methods  ************************************************
     *****************************************************************************************************************/
    /**
     * To set the tooltip message
     *
     * @param message
     */
    public void setMessage(String message) {
        this.message = message;
        tooltip.setToolTipMessage(message);
    }

    /**
     * To set the tooltip title, if is null or empty, the title view is set on visibility = GONE
     *
     * @param title
     */
    public void setTitle(String title) {
        this.title = title;
        tooltip.setToolTipTitle(title);
    }


}
