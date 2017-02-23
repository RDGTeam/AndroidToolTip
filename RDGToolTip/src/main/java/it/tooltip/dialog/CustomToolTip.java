package it.tooltip.dialog;


import android.app.Activity;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Rect;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;

import it.tooltip.R;
import it.tooltip.callback.RDGCallback;
import it.tooltip.closeManager.ClosePolicy;
import it.tooltip.position.ToolTipPositionManager;

public class CustomToolTip extends DialogFragment {

    //context
    private Context context;

    private final String TAG = "CustomToolTip----->";

    //fragment dialog
    private Dialog rawToolTip;
    //custom view of the dialog
    private View view;

    //view's elements
    private TextView tooltipTitle;
    private TextView tooltipBodyMessage;
    private View arrowDown;
    private View arrowUp;
    private View arrowLeft;
    private View arrowRight;
    private LinearLayout tooltipCloseButton;
    private LinearLayout tooltipHeader;

    private View tooltipContainer;

    private RDGCallback callback;

    private Window window;

    //array which will contain the anchorView location
    private int[] locationOnScreen = new int[2];
    private int anchorViewXcoordinate;
    private int anchorViewYcoordinate;

    private View customView;

    //dim effect
    private boolean dim;

    //offSet of screen TOOLBAR
    private int topOffset;

    public CustomToolTip() {
    }

    /*******************************************************************************************************************
     * ************************************************  Public Methods  ************************************************
     *******************************************************************************************************************/
    /**
     * @param callback
     */
    public void setOnCloseListener(RDGCallback callback) {
        this.callback = callback;
    }

    /**
     * To set the message in the tooltip
     *
     * @param message
     */
    public void setToolTipMessage(String message) {
        if (tooltipBodyMessage != null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                this.tooltipBodyMessage.setText(Html.fromHtml(message, Html.FROM_HTML_MODE_LEGACY));
            } else {
                this.tooltipBodyMessage.setText(Html.fromHtml(message));
            }
        }
    }

    /**
     * to set the title, if is null, the title view in set to visibility=GONE
     *
     * @param message
     */
    public void setToolTipTitle(String message) {
        if (tooltipTitle != null) {
            if (!message.trim().equals("")) {
                this.tooltipTitle.setVisibility(View.VISIBLE);
                this.tooltipHeader.setVisibility(View.VISIBLE);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    this.tooltipTitle.setText(Html.fromHtml(message, Html.FROM_HTML_MODE_LEGACY));
                } else {
                    this.tooltipTitle.setText(Html.fromHtml(message));
                }
            }
        }
    }

    /**
     * Set the close policy of the dialog
     * For example: close with outside tap, close with tap on dialog, close throught a button...
     *
     * @param closePolicy
     */
    public void setClosePolicy(ClosePolicy closePolicy) {
        printInfo("The close policy is = " + closePolicy);
        switch (closePolicy) {
            case CLOSE_INSIDE_TAP:
                setCancelable(false);
                view.setOnClickListener(getOnCloseListener());
                break;
            case CLOSE_OUTSIDE_TAP:
                setCancelable(true);
                break;
            case CLOSE_ANY_TAP:
                setCancelable(true);
                view.setOnClickListener(getOnCloseListener());
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
        rawToolTip.requestWindowFeature(Window.FEATURE_NO_TITLE);
        printInfo("Dialog created");
        view = ((Activity) context).getLayoutInflater().inflate(R.layout.tooltip_layout, null);
        printInfo("View setted on dialog");

        //xml references
        getXmlReferences();

        /* set to true the default value of isCancelable*/
        setClosePolicy(ClosePolicy.CLOSE_OUTSIDE_TAP);

        //get the window where the tooltip is drawn
        window = rawToolTip.getWindow();

        printInfo("Dialog created");
    }

    /**
     * Method that create the dialog from the given view
     *
     * @param context
     * @param view
     */
    public void createToolTip(Context context, View view) {
        printInfo("Constructor called");
        this.context = context;
        this.view = view;
        this.customView = view;
        rawToolTip = new Dialog(context);
        rawToolTip.requestWindowFeature(Window.FEATURE_NO_TITLE);
        printInfo("Dialog created");
        printInfo("View setted on dialog");

        /* set to true the default value of isCancelable*/
        setClosePolicy(ClosePolicy.CLOSE_OUTSIDE_TAP);

        //get the window where the tooltip is drawn
        window = rawToolTip.getWindow();

        printInfo("Dialog created");
    }


    /**
     * Method that create the dialog from the given int resource
     *
     * @param context
     * @param customResource
     */
    public void createToolTip(Context context, int customResource) {
        printInfo("Constructor called");
        this.context = context;
        rawToolTip = new Dialog(context);
        rawToolTip.requestWindowFeature(Window.FEATURE_NO_TITLE);
        printInfo("Dialog created");
        this.view = ((Activity) context).getLayoutInflater().inflate(customResource, null);
        this.customView = view;
        printInfo("View setted on dialog");

        /* set to true the default value of isCancelable*/
        setClosePolicy(ClosePolicy.CLOSE_OUTSIDE_TAP);

        //get the window where the tooltip is drawn
        window = rawToolTip.getWindow();

        printInfo("Dialog created");
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
            recalculateDialogPosition();

            view.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                @Override
                public void onGlobalLayout() {

                    centerDialogToView(anchorView);

                    WindowManager.LayoutParams params = window.getAttributes();
                    int tooltipWidth;
                    int tooltipHeight;
                    if (tooltipContainer != null) {
                        tooltipWidth = tooltipContainer.getWidth();
                        tooltipHeight = tooltipContainer.getHeight();
                    } else {
                        tooltipWidth = view.getWidth();
                        tooltipHeight = view.getHeight();
                    }
                    switch (position) {

                        case BOTTOM:
                            if (arrowDown != null) {
                                params.y += (tooltipHeight / 2) + (anchorView.getHeight() / 2) - (arrowDown.getHeight() / 2);
                                showArrow(position);
                            } else {
                                params.y += (tooltipHeight / 2) + (anchorView.getHeight() / 2);
                            }
                            break;
                        case LEFT:
                            if (arrowLeft != null) {
                                params.x -= (tooltipWidth / 2) + (anchorView.getWidth() / 2) - (arrowLeft.getWidth() / 2);
                                showArrow(position);
                            } else {
                                params.x -= (tooltipWidth / 2) + (anchorView.getWidth() / 2);
                            }
                            break;
                        case RIGHT:
                            if (arrowRight != null) {
                                params.x += (tooltipWidth / 2) + (anchorView.getWidth() / 2) - (arrowRight.getWidth() / 2);
                                showArrow(position);
                            } else {
                                params.x += (tooltipWidth / 2) + (anchorView.getWidth() / 2);
                            }
                            break;
                        case TOP:
                            if (arrowUp != null) {
                                params.y -= (tooltipHeight / 2) + (anchorView.getHeight() / 2) - (arrowUp.getHeight() / 2);
                                showArrow(position);
                            } else {
                                params.y -= (tooltipHeight / 2) + (anchorView.getHeight() / 2);
                            }
                            break;
                        default:
                            if (customView == null) {
                                showArrow(ToolTipPositionManager.NONE);
                            }
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
            printInfo("setToolTipPosition = dialog positioned");
        } else {
            printInfo("setToolTipPosition = anchorView is null");
        }
    }
    /**
     * hide/show the dim effect
     *
     * @param dim
     */
    public void setDimEffect(Boolean dim) {
        this.dim = dim;
    }
    /**********************************************************************************************
     * ************************************************  @Override Methods* ************************************************
     **********************************************************************************************/
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        rawToolTip.setContentView(view);
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        if (dim) {
            /*not to apply the successive anti dimmed effect to the general activity window*/
            rawToolTip.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
            /*remove the dimmed background to the dialog*/
            rawToolTip.getWindow().setDimAmount(0);
            printInfo("onCreateDialog = dim amount setted to 0");
        }
        rawToolTip.getWindow().setBackgroundDrawable(new ColorDrawable(0));
        printInfo("onCreateDialog = clearFlags applied");

        return rawToolTip;
    }

    @Override
    public void onCancel(DialogInterface dialog) {
        if (callback != null) {
            callback.callback(null);
        }
    }

    /**********************************************************************************************
     * ************************************************  Private Methods  *************************
     **********************************************************************************************/

    /**
     * insert here all features the dialog should have/activate
     */
    private void setDialogFeatures() {
    }

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


        printInfo("anchorViewX " + anchorViewXcoordinate);
        printInfo("anchorViewY " + anchorViewYcoordinate);

        int anchorViewXcenter = anchorViewXcoordinate + anchorView.getWidth() / 2;
        int anchorViewYcenter = anchorViewYcoordinate + anchorView.getHeight() / 2;

        printInfo("anchorViewXcenter " + anchorViewXcenter);
        printInfo("anchorViewYcenter " + anchorViewYcenter);

        WindowManager.LayoutParams params = window.getAttributes();

        printInfo("tooltip x coordinate = " + params.x);
        printInfo("tooltip y coordinate =" + params.y);

        int tooltipWidth;
        int tooltipHeight;
        if (tooltipContainer != null) {
            tooltipWidth = tooltipContainer.getWidth();
            tooltipHeight = tooltipContainer.getHeight();
        } else {
            tooltipWidth = view.getWidth();
            tooltipHeight = view.getHeight();
        }

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
        printInfo("Calculating Top Bar offset....");
        Rect rectangle = new Rect();
        Window w = ((Activity) context).getWindow();
        w.getDecorView().getWindowVisibleDisplayFrame(rectangle);
        int statusBarHeight = rectangle.top;
        topOffset = statusBarHeight;
    }

    /**
     * First version of the method : try to overlap the anchor view
     */
    private void recalculateDialogPosition() {
        printInfo("Calculating Dialog Position....");
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
                if (callback != null) {
                    callback.callback(null);
                }
                rawToolTip.dismiss();
            }
        };
    }


    private void printInfo(String message) {
        Log.d(TAG, message);
    }
}
