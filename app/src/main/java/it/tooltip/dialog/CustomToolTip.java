package it.tooltip.dialog;


import android.app.Activity;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.text.Html;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;

import it.tooltip.R;
import it.tooltip.closeManager.ClosePolicy;
import it.tooltip.position.ToolTipPositionManager;

public class CustomToolTip extends DialogFragment {

    private Context context;
    private final String TAG = "CustomToolTip----->";

    //fragment dialog
    private Dialog rawToolTip;
    //custom view of the dialog
    private View view;

    //vie's elements
    private TextView tooltipTitle;
    private TextView tooltipBodyMessage;
    private View arrowDown;
    private View arrowUp;
    private View arrowLeft;
    private View arrowRight;
    private LinearLayout tooltipCloseButton;
    private LinearLayout tooltipHeader;

    private View tooltipContainer;


    private Window window;

    //array which will contain the anchorView location
    private int[] locationOnScreen = new int[2];
    private int anchorViewXcoordinate;
    private int anchorViewYcoordinate;


    //offSet of screen TOOLBAR
    private int topOffset;

    public CustomToolTip() {
    }

    /*******************************
     * Public Methods
     *********************************/


    public void setToolTipMessage(String message) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            this.tooltipBodyMessage.setText(Html.fromHtml(message, Html.FROM_HTML_MODE_LEGACY));
        } else {
            this.tooltipBodyMessage.setText(Html.fromHtml(message));
        }
    }

    public void setToolTipTitle(String message) {
        if (!message.trim().isEmpty()) {
            this.tooltipTitle.setVisibility(View.VISIBLE);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                this.tooltipTitle.setText(Html.fromHtml(message, Html.FROM_HTML_MODE_LEGACY));
            } else {
                this.tooltipTitle.setText(Html.fromHtml(message));
            }
        }
    }

    /**
     * @param closePolicy
     */
    public void setClosePolicy(ClosePolicy closePolicy) {
        switch (closePolicy) {
            case CLOSE_ON_TAP:
                setCancelable(false);
                view.setOnClickListener(getOnCloseListener());
                break;
            case CLOSE_OUTSIDE_TAP:
                setCancelable(true);
                break;
            case NO_CLOSE:
                setCancelable(false);
                break;
        }
    }

    /**
     * Method wich get the Layout inflater from context and inflate the view
     *
     * @param context
     */
    public void createToolTip(Context context) {
        printInfo("Constructor called");
        this.context = context;
        rawToolTip = new Dialog(context);
        printInfo("Dialog created");
        view = ((Activity) context).getLayoutInflater().inflate(R.layout.tooltip_layout, null);
        printInfo("View setted on dialog");

        //xml references
        getXmlReferences();

        /* set to true the default value of isCancelable*/
        setClosePolicy(ClosePolicy.CLOSE_OUTSIDE_TAP);

        //get the window where the tooltip is drawn
        window = rawToolTip.getWindow();


    }

    /**
     * with this method is possible draw the tooltip on the ancherView at the given position
     *
     * @param anchorView
     * @param position
     */
    public void setToolTipPosition(final View anchorView, final ToolTipPositionManager position) {
        if (anchorView != null) {

            anchorView.getLocationOnScreen(locationOnScreen);
            anchorViewXcoordinate = locationOnScreen[0];
            anchorViewYcoordinate = locationOnScreen[1];

            calculateTopBarOffset();
            recalculateDialogPosition(anchorView);

            view.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                @Override
                public void onGlobalLayout() {

                    centerDialogToView(anchorView);

                    WindowManager.LayoutParams params = window.getAttributes();
                    int tooltipWidth = tooltipContainer.getWidth();
                    int tooltipHeight = tooltipContainer.getHeight();
                    switch (position) {

                        case BOTTOM:
                            params.y += (tooltipHeight / 2) + (anchorView.getHeight() / 2) - (arrowDown.getHeight() / 2);
                            showArrow(position);
                            break;
                        case LEFT:
                            params.x -= (tooltipWidth / 2) + (anchorView.getWidth() / 2) - (arrowLeft.getWidth() / 2);
                            showArrow(position);
                            break;
                        case RIGHT:
                            params.x += (tooltipWidth / 2) + (anchorView.getWidth() / 2) - (arrowRight.getWidth() / 2);
                            showArrow(position);
                            break;
                        case TOP:
                            params.y -= (tooltipHeight / 2) + (anchorView.getHeight() / 2) - (arrowUp.getHeight() / 2);
                            showArrow(position);
                            break;
                        default:
                            showArrow(ToolTipPositionManager.NONE);
                            break;

                    }
                    window.setAttributes(params);
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                        view.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                    } else {
                        view.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                    }
                }
            });
        } else {
            printInfo("setToolTipPosition = anchorView is null");
        }
    }

    /*******************************
     * @Override Methods
     *********************************/
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        rawToolTip.setContentView(view);
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        /*not to apply the successive anti dimmed effect to the general activity window*/
        rawToolTip.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
        rawToolTip.getWindow().setBackgroundDrawable(new ColorDrawable(0));
        printInfo("onCreateDialog = clearFlags applied");

        /*remove the dimmed background to the dialog*/
        rawToolTip.getWindow().setDimAmount(0);

        printInfo("onCreateDialog = dim amount setted to 0");

        return rawToolTip;
    }

    /*******************************
     * Private Methods
     *********************************/
    /**
     * Show the  arrow base on position, hiding the other
     *
     * @param position
     */
    private void showArrow(ToolTipPositionManager position) {
        hideArrows();
        switch (position) {
            case BOTTOM:
                arrowUp.setVisibility(View.VISIBLE);
                break;
            case LEFT:
                arrowRight.setVisibility(View.VISIBLE);
                break;
            case RIGHT:
                arrowLeft.setVisibility(View.VISIBLE);
                break;
            case TOP:
                arrowDown.setVisibility(View.VISIBLE);
                break;
            default:
                break;
        }
    }

    /**
     * Method to get all references to the xml resources
     */
    private void getXmlReferences() {
        tooltipTitle = (TextView) view.findViewById(R.id.tooltip_title);
        tooltipBodyMessage = (TextView) view.findViewById(R.id.tooltip_body_message);
        tooltipCloseButton = (LinearLayout) view.findViewById(R.id.tooltip_close_button);
        tooltipHeader = (LinearLayout) view.findViewById(R.id.tooltip_header);
        arrowDown = view.findViewById(R.id.arrow_down);
        arrowUp = view.findViewById(R.id.arrow_up);
        arrowLeft = view.findViewById(R.id.arrow_left);
        arrowRight = view.findViewById(R.id.arrow_right);
        tooltipContainer = view.findViewById(R.id.tooltip_id);
    }

    /**
     * hides all arrow views
     */
    private void hideArrows() {
        arrowRight.setVisibility(View.INVISIBLE);
        arrowLeft.setVisibility(View.INVISIBLE);
        arrowUp.setVisibility(View.INVISIBLE);
        arrowDown.setVisibility(View.INVISIBLE);
    }

    /**
     * Place the tooltip on the anchorView centering both
     *
     * @param anchorView
     */
    private void centerDialogToView(View anchorView) {


        printInfo("anchorViewXanchorView " + anchorViewXcoordinate);
        printInfo("anchorViewYanchorView " + anchorViewYcoordinate);

        int anchorViewXcenter = anchorViewXcoordinate + anchorView.getWidth() / 2;
        int anchorViewYcenter = anchorViewYcoordinate + anchorView.getHeight() / 2;

        printInfo("anchorViewXcenter " + anchorViewXcenter);
        printInfo("anchorViewYcenter " + anchorViewYcenter);

        WindowManager.LayoutParams params = window.getAttributes();

        printInfo("tooltip x coordinate = " + params.x);
        printInfo("tooltip y coordinate =" + params.y);

        int tooltipWidth = tooltipContainer.getWidth();
        int tooltipHeight = tooltipContainer.getHeight();

        printInfo("tooltipWidth " + tooltipWidth);
        printInfo("tooltipHeight " + tooltipHeight);

        int newTX = anchorViewXcenter - (tooltipWidth / 2);
        int newTY = anchorViewYcenter - (tooltipHeight / 2) - topOffset;

        printInfo("new tooltip x coordinate = " + newTX);
        printInfo("new tooltip y coordinate =" + newTY);

        params.x = newTX;
        params.y = newTY;
        window.setAttributes(params);
    }

    /**
     * private method to calculate the top toolbar offset
     */
    private void calculateTopBarOffset() {
        DisplayMetrics dm = new DisplayMetrics();
        ((Activity) context).getWindowManager().getDefaultDisplay().getMetrics(dm);

        final ViewGroup viewGroup = (ViewGroup) ((ViewGroup) ((Activity) context).findViewById(android.R.id.content)).getChildAt(0);

        topOffset = dm.heightPixels - viewGroup.getMeasuredHeight();
    }

    /**
     * First version of the method : try to overlap the anchor view
     *
     * @param anchorView
     */
    private void recalculateDialogPosition(View anchorView) {
        window.setGravity(Gravity.LEFT | Gravity.TOP);
        WindowManager.LayoutParams params = window.getAttributes();
        params.x = anchorViewXcoordinate;
        params.y = anchorViewYcoordinate - topOffset;
        window.setAttributes(params);
    }


    /**
     * Listener to dismiss the dialog
     *
     * @return
     */
    private View.OnClickListener getOnCloseListener() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                rawToolTip.dismiss();
            }
        };
    }


    private void printInfo(String message) {
        Log.d(TAG, message);
    }
}
