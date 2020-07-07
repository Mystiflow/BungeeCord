package net.md_5.bungee.api.chat;

import static net.md_5.bungee.api.NbtUtil.has;
import com.google.common.base.Preconditions;
import java.util.ArrayList;
import java.util.List;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.Singular;
import lombok.ToString;
import net.md_5.bungee.api.NbtUtil;
import net.md_5.bungee.chat.ComponentSerializer;
import se.llbit.nbt.ByteTag;
import se.llbit.nbt.CompoundTag;
import se.llbit.nbt.IntTag;
import se.llbit.nbt.ListTag;
import se.llbit.nbt.SpecificTag;
import se.llbit.nbt.StringTag;
import se.llbit.nbt.Tag;

/**
 * Metadata for use in conjunction with {@link HoverEvent.Action#SHOW_ITEM}
 */
@ToString(of = "nbt")
@EqualsAndHashCode(of = "nbt")
@Getter
public final class ItemTag
{

    private String nbt;
    /**
     * The properties of this ItemTag.
     *
     * Mojangson parsing currently unsupported; therefore;
     *
     * If this was constructed using {@link #ofNbt(String)} then {@link #getProperties()}
     * will return blank contents. If you intend to work with properties, create the ItemTag
     * using the {@link #ofProperties(PropertyInfo)} constructor.
      */
    private PropertyInfo properties;

    private ItemTag()
    {
    }

    @EqualsAndHashCode
    @ToString
    @Getter
    @Setter
    @lombok.Builder(builderClassName = "Builder")
    public static class PropertyInfo
    {

        private BaseComponent[] name;
        @Singular("enchantment")
        private List<Enchantment> enchantments;
        @Singular("lore")
        private List<BaseComponent[]> lore;
        private Boolean unbreakable;

        @Getter
        @RequiredArgsConstructor
        @ToString
        public static class Enchantment
        {

            private final String id;
            private final int level;
        }

        public void copyTo(PropertyInfo propertyInfo)
        {
            propertyInfo.name = name;
            propertyInfo.enchantments = new ArrayList<>( enchantments );
            propertyInfo.lore = new ArrayList<>( lore );
            propertyInfo.unbreakable = unbreakable;
        }
    }

    public static ItemTag ofProperties(PropertyInfo properties)
    {
        ItemTag itemTag = new ItemTag();
        itemTag.properties = Preconditions.checkNotNull( properties, "properties" );
        itemTag.nbt = NbtUtil.toString( itemTag.tagFromProperties() );
        itemTag.restoreLists();
        return itemTag;
    }

    public static ItemTag ofNbt(String nbt)
    {
        ItemTag itemTag = new ItemTag();
        itemTag.nbt = Preconditions.checkNotNull( nbt, "nbt" );
        // TODO can add this once fromString is implemented
        // updatePropertiesFromTag( NbtUtil.fromString( nbt ) )
        itemTag.properties = PropertyInfo.builder().build(); // just use dummy properties for now
        return itemTag;
    }

    private void restoreLists()
    {
        // Lombok converts it to AbstractList, we want ArrayList
        properties.enchantments = new ArrayList<>( properties.enchantments );
        properties.lore = new ArrayList<>( properties.lore );
    }

    protected void updatePropertiesFromTag(CompoundTag tag)
    {
        if ( has( tag, "Enchantments" ) )
        {
            ListTag enchList = tag.get( "Enchantments" ).asList();
            for ( SpecificTag ench : enchList )
            {
                CompoundTag key = ench.asCompound();
                properties.enchantments.add( new PropertyInfo.Enchantment(
                        key.get( "id" ).stringValue(), key.get( "lvl" ).intValue()
                ) );
            }
        }
        if ( has( tag, "Unbreakable" ) )
        {
            int val = tag.get( "Unbreakable" ).byteValue();
            if ( val == 0 )
            {
                properties.unbreakable = Boolean.FALSE;
            } else if ( val == 1 )
            {
                properties.unbreakable = Boolean.TRUE;
            }
        }
        if ( has( tag, "display" ) )
        {
            CompoundTag display = tag.get( "display" ).asCompound();
            if ( has( display, "Name" ) )
            {
                properties.name = ComponentSerializer.parse( display.get( "Name" ).stringValue() );
            }
            if ( has( display, "lore" ) )
            {
                ListTag loreList = display.get( "lore" ).asList();
                int i = 0;
                for ( SpecificTag itLore : loreList )
                {
                    BaseComponent[] temp;
                    properties.lore.add( temp = ComponentSerializer.parse( itLore.stringValue() ) );
                }
            }
        }
    }

    protected CompoundTag tagFromProperties()
    {
        Preconditions.checkNotNull( properties, "properties" );

        CompoundTag headTag = new CompoundTag();
        if ( !properties.enchantments.isEmpty() )
        {
            ListTag enchArray = new ListTag( CompoundTag.TAG_COMPOUND, new ArrayList<>() );
            for ( PropertyInfo.Enchantment ench : properties.enchantments )
            {
                CompoundTag enchObj = new CompoundTag();
                enchObj.add( "id", new StringTag( ench.id ) );
                enchObj.add( "lvl", new IntTag( ench.level ) );
                enchArray.add( enchObj );
            }
            headTag.add( "Enchantments", enchArray );
        }

        if ( properties.unbreakable != null )
        {
            headTag.add( "Unbreakable", new ByteTag( ( properties.unbreakable ) ? 1 : 0 ) );
        }

        CompoundTag display = new CompoundTag();

        if ( properties.name != null )
        {
            display.add( "Name", new StringTag( ComponentSerializer.toString( properties.name ) ) );
        }

        if ( !properties.lore.isEmpty() )
        {
            ListTag lore = new ListTag( Tag.TAG_STRING, new ArrayList<>() );
            for ( BaseComponent[] itLore : properties.lore )
            {
                lore.add( new StringTag( ComponentSerializer.toString( itLore ) ) );
            }
            display.add( "lore", lore );
        }

        if ( display.size() != 0 )
        {
            headTag.add( "display", display );
        }

        return headTag;
    }
}
