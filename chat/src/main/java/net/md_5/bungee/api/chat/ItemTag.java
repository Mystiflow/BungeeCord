package net.md_5.bungee.api.chat;

import com.google.common.base.Preconditions;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import net.md_5.bungee.api.NbtUtil;
import se.llbit.nbt.CompoundTag;

/**
 * Metadata for use in conjunction with {@link HoverEvent.Action#SHOW_ITEM}
 */
@ToString(of = "nbt")
@EqualsAndHashCode(of = "nbt")
@Getter
public final class ItemTag
{

    private static final List<TagHandler> CUSTOM_HANDLERS = new ArrayList<>();

    public static void addHandler(TagHandler property)
    {
        CUSTOM_HANDLERS.add( property );
    }

    protected String nbt;
    protected ItemPropertyInfo properties;

    protected ItemTag()
    {
    }

    /**
     * API for plugins to create custom handlers in addition to the default ones.
     * e.g. trivial things not included: e.g. Leather Armor Color.
     */
    public interface TagHandler
    {

        void serialise(CompoundTag root);

        void deserialise(CompoundTag root);
    }

    public static ItemTag ofNbt(String nbt)
    {
        ItemTag itemTag = new ItemTag();
        itemTag.nbt = Preconditions.checkNotNull( nbt, "nbt" );
        return itemTag;
    }

    protected void serialise(CompoundTag tag)
    {
        serialise( tag, Collections.singletonList( properties ) );
    }

    protected void serialise(CompoundTag tag, List<TagHandler> handlers)
    {
        Preconditions.checkNotNull( tag, "tag" );
        for ( TagHandler handler : handlers )
        {
            handler.serialise( tag );
        }
    }

    protected CompoundTag deserialise()
    {
        return deserialise( CUSTOM_HANDLERS );
    }

    protected CompoundTag deserialise(List<TagHandler> handlers)
    {
        Preconditions.checkNotNull( properties, "properties" );
        CompoundTag root = new CompoundTag();
        for ( TagHandler handler : handlers )
        {
            handler.deserialise( root );
        }
        return root;
    }

    public String getNbt()
    {
        if ( nbt == null && properties != null )
        {
            CompoundTag tag = new CompoundTag();
            // write the properties to compound
            serialise( tag, Collections.singletonList( properties ) );
            // convert the compound to SNBT
            nbt = NbtUtil.toString( tag );
        }
        return nbt;
    }

    public ItemPropertyInfo getProperties()
    {
        if ( properties == null && nbt != null )
        {
            throw new UnsupportedOperationException( "Cannot obtain properties from tag constructed by NBT. "
                    + "This will be available in future update. Use getNbt() if you wish to work on this." );
            // TODO Convert NB T to compound using Mojangson then deserialise to PropertyInfo
        }
        return properties;
    }
}
