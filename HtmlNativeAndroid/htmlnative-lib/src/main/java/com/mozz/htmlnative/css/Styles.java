package com.mozz.htmlnative.css;

import android.content.Context;
import android.graphics.Matrix;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsoluteLayout;

import com.mozz.htmlnative.dom.DomElement;
import com.mozz.htmlnative.HNLog;
import com.mozz.htmlnative.HNSandBoxContext;
import com.mozz.htmlnative.HNative;
import com.mozz.htmlnative.InheritStyleStack;
import com.mozz.htmlnative.attrs.AttrHandler;
import com.mozz.htmlnative.attrs.AttrsHelper;
import com.mozz.htmlnative.attrs.Background;
import com.mozz.htmlnative.attrs.LayoutAttrHandler;
import com.mozz.htmlnative.common.PixelValue;
import com.mozz.htmlnative.common.Utils;
import com.mozz.htmlnative.exception.AttrApplyException;
import com.mozz.htmlnative.view.BackgroundViewDelegate;

/**
 * @author Yang Tao, 17/3/30.
 */
public final class Styles {

    private static final String TAG = Styles.class.getSimpleName();

    private Styles() {
    }

    public static final String ATTR_STYLE = "style";
    public static final String ATTR_WIDTH = "width";
    public static final String ATTR_HEIGHT = "height";
    public static final String ATTR_BACKGROUND = "background";
    public static final String ATTR_PADDING = "padding";
    public static final String ATTR_PADDING_LEFT = "padding-left";
    public static final String ATTR_PADDING_RIGHT = "padding-right";
    public static final String ATTR_PADDING_TOP = "padding-top";
    public static final String ATTR_PADDING_BOTTOM = "padding-bottom";
    public static final String ATTR_MARGIN = "margin";
    public static final String ATTR_MARGIN_LEFT = "margin-left";
    public static final String ATTR_MARGIN_RIGHT = "margin-right";
    public static final String ATTR_MARGIN_TOP = "margin-top";
    public static final String ATTR_MARGIN_BOTTOM = "margin-bottom";
    public static final String ATTR_LEFT = "left";
    public static final String ATTR_TOP = "top";
    public static final String ATTR_ALPHA = "alpha";
    public static final String ATTR_ONCLICK = "onclick";
    public static final String ATTR_VISIBLE = "visibility";
    public static final String ATTR_DISPLAY = "display";
    public static final String ATTR_DIRECTION = "direction";

    public static final String VAL_FILL_PARENT = "100%";

    public static final String VAL_DISPLAY_FLEX = "flex";
    public static final String VAL_DISPLAY_BOX = "box";
    public static final String VAL_DISPLAY_ABSOLUTE = "absolute";

    static {
        InheritStylesRegistry.register(ATTR_VISIBLE);
        InheritStylesRegistry.register(ATTR_DIRECTION);
    }

    /**
     * Apply a params with value to a view
     *
     * @param context        {@link Context}
     * @param sandBoxContext {@link HNSandBoxContext}
     * @param v              {@link View} view to be processed
     * @param domElement
     * @param layoutParams   {@link ViewGroup.LayoutParams}, layoutParams for parent
     *                       when add this view to parent
     * @param parent         {@link ViewGroup}, parent of the view
     * @param params         parameter name
     * @param value          parameter value     @throws AttrApplyException
     */
    public static void applyStyle(Context context, final HNSandBoxContext sandBoxContext, View v,
                                  DomElement domElement, @NonNull ViewGroup.LayoutParams
                                          layoutParams, @NonNull ViewGroup parent, AttrHandler
                                          viewAttrHandler, AttrHandler extraAttrHandler,
                                  LayoutAttrHandler parentAttr, String params, Object value,
                                  boolean isParent, InheritStyleStack stack) throws
            AttrApplyException {

        HNLog.d(HNLog.STYLE, "before apply " + params + " = " + value.toString() + " to " + v + "" +
                "(" + domElement.getType() + ")");

        if (isParent) {
            if (!InheritStylesRegistry.isInherit(params)) {
                return;
            }
        }

        HNLog.d(HNLog.STYLE, "apply " + params + " = " + value.toString() + " to " + v + "(" +
                domElement.getType() + ")");

        switch (params) {
            case ATTR_WIDTH: {
                if (value.toString().equalsIgnoreCase(VAL_FILL_PARENT)) {
                    layoutParams.width = ViewGroup.LayoutParams.MATCH_PARENT;
                } else {
                    PixelValue pixel = Utils.toPixel(value);
                    layoutParams.width = (int) pixel.getPxValue();
                }
            }
            break;

            case ATTR_HEIGHT: {
                if (value.toString().equalsIgnoreCase(VAL_FILL_PARENT)) {
                    layoutParams.height = ViewGroup.LayoutParams.MATCH_PARENT;
                } else {
                    PixelValue pixel = Utils.toPixel(value);
                    layoutParams.height = (int) pixel.getPxValue();
                }
            }
            break;

            case ATTR_BACKGROUND:
                if (value instanceof Background) {
                    Background background = (Background) value;

                    if (!TextUtils.isEmpty(background.getUrl())) {
                        Matrix matrix = Background.createBitmapMatrix(background);
                        HNative.getImageViewAdapter().setImage(background.getUrl(), new
                                BackgroundViewDelegate(v, matrix, background.getColor()));
                    } else if (background.isColorSet()) {
                        v.setBackgroundColor(background.getColor());
                    }
                } else {
                    throw new AttrApplyException("Background style is wrong when parsing.");
                }

                break;

            case ATTR_MARGIN: {
                if (layoutParams instanceof ViewGroup.MarginLayoutParams) {
                    PixelValue[] pixelValues = Utils.pixelGroups(value.toString());
                    int top = -1;
                    int bottom = -1;
                    int left = -1;
                    int right = -1;
                    if (pixelValues.length == 1) {
                        top = bottom = left = right = (int) pixelValues[0].getValue();
                    } else if (pixelValues.length == 2) {
                        top = bottom = (int) pixelValues[0].getValue();
                        left = right = (int) pixelValues[1].getValue();
                    } else if (pixelValues.length == 4) {
                        top = (int) pixelValues[0].getValue();
                        bottom = (int) pixelValues[2].getValue();
                        left = (int) pixelValues[3].getValue();
                        right = (int) pixelValues[1].getValue();
                    }
                    if (top != -1 && bottom != -1 && left != -1 && right != -1) {
                        ((ViewGroup.MarginLayoutParams) layoutParams).setMargins(left, top,
                                right, bottom);
                    }
                }
            }
            break;

            case ATTR_MARGIN_RIGHT:
                if (layoutParams instanceof ViewGroup.MarginLayoutParams) {
                    ViewGroup.MarginLayoutParams marginLayoutParams = (ViewGroup
                            .MarginLayoutParams) layoutParams;
                    int left = marginLayoutParams.leftMargin;
                    int right = Utils.toInt(value);
                    int top = marginLayoutParams.topMargin;
                    int bottom = marginLayoutParams.bottomMargin;

                    marginLayoutParams.setMargins(left, top, right, bottom);
                }
                break;

            case ATTR_MARGIN_LEFT:
                if (layoutParams instanceof ViewGroup.MarginLayoutParams) {
                    ViewGroup.MarginLayoutParams marginLayoutParams = (ViewGroup
                            .MarginLayoutParams) layoutParams;
                    int left = Utils.toInt(value);
                    int right = marginLayoutParams.rightMargin;
                    int top = marginLayoutParams.topMargin;
                    int bottom = marginLayoutParams.bottomMargin;
                    marginLayoutParams.setMargins(left, top, right, bottom);
                }
                break;

            case ATTR_MARGIN_TOP:
                if (layoutParams instanceof ViewGroup.MarginLayoutParams) {
                    ViewGroup.MarginLayoutParams marginLayoutParams = (ViewGroup
                            .MarginLayoutParams) layoutParams;
                    int left = marginLayoutParams.leftMargin;
                    int right = marginLayoutParams.rightMargin;
                    int top = Utils.toInt(value);
                    int bottom = marginLayoutParams.bottomMargin;

                    marginLayoutParams.setMargins(left, top, right, bottom);
                }
                break;

            case ATTR_MARGIN_BOTTOM:
                if (layoutParams instanceof ViewGroup.MarginLayoutParams) {
                    ViewGroup.MarginLayoutParams marginLayoutParams = (ViewGroup
                            .MarginLayoutParams) layoutParams;
                    int left = marginLayoutParams.leftMargin;
                    int right = marginLayoutParams.rightMargin;
                    int top = marginLayoutParams.topMargin;
                    int bottom = Utils.toInt(value);

                    marginLayoutParams.setMargins(left, top, right, bottom);
                }
                break;

            case ATTR_PADDING: {
                PixelValue[] pixelValues = Utils.pixelGroups(value.toString());
                int top = -1;
                int bottom = -1;
                int left = -1;
                int right = -1;
                if (pixelValues.length == 1) {
                    top = bottom = left = right = (int) pixelValues[0].getValue();
                } else if (pixelValues.length == 2) {
                    top = bottom = (int) pixelValues[0].getValue();
                    left = right = (int) pixelValues[1].getValue();
                } else if (pixelValues.length == 4) {
                    top = (int) pixelValues[0].getValue();
                    bottom = (int) pixelValues[2].getValue();
                    left = (int) pixelValues[3].getValue();
                    right = (int) pixelValues[1].getValue();
                }
                if (top != -1 && bottom != -1 && left != -1 && right != -1) {
                    v.setPadding(left, top, right, bottom);
                }
            }
            break;

            case ATTR_PADDING_LEFT:
                int paddingLeft = Utils.toInt(value);
                AttrsHelper.setLeftPadding(v, paddingLeft);
                break;

            case ATTR_PADDING_RIGHT:
                int paddingRight = Utils.toInt(value);
                AttrsHelper.setRightPadding(v, paddingRight);
                break;

            case ATTR_PADDING_TOP:
                int paddingTop = Utils.toInt(value);
                AttrsHelper.setTopPadding(v, paddingTop);
                break;

            case ATTR_PADDING_BOTTOM:
                int paddingBottom = Utils.toInt(value);
                AttrsHelper.setBottomPadding(v, paddingBottom);
                break;

            case ATTR_LEFT:
                if (layoutParams instanceof AbsoluteLayout.LayoutParams) {
                    ((AbsoluteLayout.LayoutParams) layoutParams).x = Utils.toInt(value);
                }
                break;

            case ATTR_TOP:
                if (layoutParams instanceof AbsoluteLayout.LayoutParams) {
                    ((AbsoluteLayout.LayoutParams) layoutParams).y = Utils.toInt(value);
                }
                break;

            case ATTR_ALPHA:
                float alpha = Utils.toFloat(value);
                v.setAlpha(alpha);
                break;

            case ATTR_VISIBLE:
                String visible = value.toString();

                if (visible.equals("visible")) {
                    v.setVisibility(View.VISIBLE);
                } else if (visible.equals("invisible")) {
                    v.setVisibility(View.INVISIBLE);
                }
                break;

            case ATTR_DIRECTION:
                String direction = value.toString();
                if (direction.equals("ltr")) {
                    v.setTextDirection(View.TEXT_DIRECTION_LTR);
                } else if (direction.equals("rtl")) {
                    v.setTextDirection(View.TEXT_DIRECTION_RTL);
                }
                break;

            case ATTR_ONCLICK:
                if (value instanceof String) {
                    final String functionName = (String) value;
                    v.setOnClickListener(new View.OnClickListener() {

                        @Override
                        public void onClick(View v) {
                            sandBoxContext.executeFun(functionName);
                        }
                    });

                }
                break;

            default:

                // If not common attrs, then
                // 1. apply the corresponding view attr first;
                // 2. apply the extra attr
                // 3. use parent view attr to this

                if (viewAttrHandler != null) {
                    viewAttrHandler.apply(context, v, domElement, parent, layoutParams, params,
                            value, isParent);
                }

                // If there extra attr is set, then should be applied also.
                if (extraAttrHandler != null) {
                    extraAttrHandler.apply(context, v, domElement, parent, layoutParams, params,
                            value, isParent);
                }

                // finally apply corresponding parent attr to child
                if (parentAttr != null) {
                    parentAttr.applyToChild(context, v, domElement, parent, layoutParams, params,
                            value, isParent);
                }
                break;
        }

        // Put inherit style into stack
        if (stack != null && InheritStylesRegistry.isInherit(params)) {
            stack.newStyle(params, value);
        }
    }

}

