package com.feed_the_beast.mods.ftbguilibrary.widget;

import com.feed_the_beast.mods.ftbguilibrary.utils.MathUtils;
import com.feed_the_beast.mods.ftbguilibrary.utils.MouseButton;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.TextFormatting;

import java.util.List;

public class ScrollBar extends Widget
{
	public enum Plane
	{
		HORIZONTAL(false),
		VERTICAL(true);

		public final boolean isVertical;

		Plane(boolean v)
		{
			isVertical = v;
		}
	}

	public final Plane plane;
	private int scrollBarSize;
	private double value = 0;
	private double scrollStep = 20;
	private double grab = -10000;
	private double minValue = 0;
	private double maxValue = 100;
	private boolean canAlwaysScroll = false;
	private boolean canAlwaysScrollPlane = true;

	public ScrollBar(Panel parent, Plane p, int ss)
	{
		super(parent);
		plane = p;
		scrollBarSize = Math.max(ss, 0);
	}

	public void setCanAlwaysScroll(boolean v)
	{
		canAlwaysScroll = v;
	}

	public void setCanAlwaysScrollPlane(boolean v)
	{
		canAlwaysScrollPlane = v;
	}

	public void setMinValue(double min)
	{
		minValue = min;
		setValue(getValue());
	}

	public double getMinValue()
	{
		return minValue;
	}

	public void setMaxValue(double max)
	{
		maxValue = max;
		setValue(getValue());
	}

	public double getMaxValue()
	{
		return maxValue;
	}

	public void setScrollStep(double s)
	{
		scrollStep = Math.max(0D, s);
	}

	public int getScrollBarSize()
	{
		return scrollBarSize;
	}

	@Override
	public boolean mousePressed(MouseButton button)
	{
		if (isMouseOver())
		{
			grab = (plane.isVertical ? (getMouseY() - (getY() + getMappedValue(height - getScrollBarSize()))) : (getMouseX() - (getX() + getMappedValue(width - getScrollBarSize()))));
			return true;
		}

		return false;
	}

	@Override
	public boolean mouseScrolled(double scroll)
	{
		if (scroll != 0 && canMouseScrollPlane() && canMouseScroll())
		{
			setValue(getValue() - getScrollStep() * scroll);
			return true;
		}

		return false;
	}

	@Override
	public void addMouseOverText(List<String> list)
	{
		if (showValueOnMouseOver())
		{
			String t = getTitle();
			list.add(t.isEmpty() ? Double.toString(getValue()) : (t + ": " + getValue()));
		}

		if (Theme.renderDebugBoxes)
		{
			list.add(TextFormatting.DARK_GRAY + "Size: " + getScrollBarSize());
			list.add(TextFormatting.DARK_GRAY + "Max: " + getMaxValue());
			list.add(TextFormatting.DARK_GRAY + "Value: " + getValue());
		}
	}

	public boolean showValueOnMouseOver()
	{
		return false;
	}

	@Override
	public void draw(Theme theme, int x, int y, int w, int h)
	{
		int scrollBarSize = getScrollBarSize();

		if (scrollBarSize > 0)
		{
			double v = getValue();

			if (grab != -10000)
			{
				if (isMouseButtonDown(MouseButton.LEFT))
				{
					if (plane.isVertical)
					{
						v = (getMouseY() - (y + grab)) * getMaxValue() / (double) (height - scrollBarSize);
					}
					else
					{
						v = (getMouseX() - (x + grab)) * getMaxValue() / (double) (width - scrollBarSize);
					}
				}
				else
				{
					grab = -10000;
				}
			}

			setValue(v);
		}

		drawBackground(theme, x, y, width, height);

		if (scrollBarSize > 0)
		{
			if (plane.isVertical)
			{
				drawScrollBar(theme, x, (int) (y + getMappedValue(height - scrollBarSize)), width, scrollBarSize);
			}
			else
			{
				drawScrollBar(theme, (int) (x + getMappedValue(width - scrollBarSize)), y, scrollBarSize, height);
			}
		}
	}

	public void drawBackground(Theme theme, int x, int y, int w, int h)
	{
		theme.drawScrollBarBackground(x, y, w, h, getWidgetType());
	}

	public void drawScrollBar(Theme theme, int x, int y, int w, int h)
	{
		theme.drawScrollBar(x, y, w, h, WidgetType.mouseOver(grab != -10000), plane.isVertical);
	}

	public void onMoved()
	{
	}

	public boolean canMouseScrollPlane()
	{
		return canAlwaysScrollPlane || isShiftKeyDown() != plane.isVertical;
	}

	public boolean canMouseScroll()
	{
		return canAlwaysScroll || isMouseOver();
	}

	public void setValue(double v)
	{
		v = MathHelper.clamp(v, getMinValue(), getMaxValue());

		if (value != v)
		{
			value = v;
			onMoved();
		}
	}

	public double getValue()
	{
		return value;
	}

	public double getMappedValue(double max)
	{
		return MathUtils.map(getMinValue(), getMaxValue(), 0, max, value);
	}

	public double getScrollStep()
	{
		return scrollStep;
	}
}