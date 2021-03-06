package com.feed_the_beast.mods.ftbguilibrary.config;

import com.feed_the_beast.mods.ftbguilibrary.icon.Color4I;
import net.minecraft.util.text.TextFormatting;

import javax.annotation.Nullable;
import java.util.List;
import java.util.function.Consumer;
import java.util.regex.Pattern;

/**
 * @author LatvianModder
 */
public class ConfigString extends ConfigFromString<String>
{
	public static final Color4I COLOR = Color4I.rgb(0xFFAA49);

	public final Pattern pattern;

	public ConfigString(@Nullable Pattern p)
	{
		pattern = p;
		defaultValue = "";
		value = "";
	}

	public ConfigString()
	{
		this(null);
	}

	@Override
	public Color4I getColor(@Nullable String v)
	{
		return COLOR;
	}

	@Override
	public boolean parse(@Nullable Consumer<String> callback, String string)
	{
		if (pattern == null || pattern.matcher(string).matches())
		{
			if (callback != null)
			{
				callback.accept(string);
			}

			return true;
		}

		return false;
	}

	@Override
	public String getStringForGUI(@Nullable String v)
	{
		return v == null ? "null" : ('"' + v + '"');
	}

	@Override
	public void addInfo(List<String> list)
	{
		super.addInfo(list);

		if (pattern != null)
		{
			list.add(TextFormatting.AQUA + "Regex: " + TextFormatting.RESET + pattern.pattern());
		}
	}
}