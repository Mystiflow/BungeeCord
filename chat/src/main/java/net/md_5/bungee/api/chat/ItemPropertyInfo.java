package net.md_5.bungee.api.chat;

import static net.md_5.bungee.api.NbtUtil.has;
import java.util.ArrayList;
import java.util.List;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.Singular;
import lombok.ToString;
import net.md_5.bungee.chat.ComponentSerializer;
import se.llbit.nbt.ByteTag;
import se.llbit.nbt.CompoundTag;
import se.llbit.nbt.IntTag;
import se.llbit.nbt.ListTag;
import se.llbit.nbt.SpecificTag;
import se.llbit.nbt.StringTag;
import se.llbit.nbt.Tag;

/**
 * This API has only been tested on 1.16.1 only, if you wish
 * to use this; it is recommended you check client versions
 * accordingly or use your own {@link net.md_5.bungee.api.chat.ItemTag.TagHandler}.
 */
@EqualsAndHashCode
@Getter
@Setter
@ToString
@Builder(builderClassName = "Builder")
public class ItemPropertyInfo implements ItemTag.TagHandler, Cloneable
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

        /**
         * Namespaced enchantment id, e.g. 'minecraft:_'
         */
        private final String id;
        private final int level;
    }

    @Override
    public void deserialise(CompoundTag root)
    {
        if ( has( root, "Enchantments" ) )
        {
            ListTag enchList = root.get( "Enchantments" ).asList();
            for ( SpecificTag ench : enchList )
            {
                CompoundTag key = ench.asCompound();
                enchantments.add( new Enchantment(
                        key.get( "id" ).stringValue(), key.get( "lvl" ).intValue()
                ) );
            }
        }
        if ( has( root, "Unbreakable" ) )
        {
            int val = root.get( "Unbreakable" ).byteValue();
            if ( val == 0 )
            {
                unbreakable = Boolean.FALSE;
            } else if ( val == 1 )
            {
                unbreakable = Boolean.TRUE;
            }
        }
        if ( has( root, "display" ) )
        {
            CompoundTag display = root.get( "display" ).asCompound();
            if ( has( display, "Name" ) )
            {
                name = ComponentSerializer.parse( display.get( "Name" ).stringValue() );
            }
            if ( has( display, "Lore" ) )
            {
                ListTag loreList = display.get( "Lore" ).asList();
                for ( SpecificTag itLore : loreList )
                {
                    lore.add( ComponentSerializer.parse( itLore.stringValue() ) );
                }
            }
        }
    }

    @Override
    public void serialise(CompoundTag root)
    {
        if ( !enchantments.isEmpty() )
        {
            ListTag enchArray = new ListTag( CompoundTag.TAG_COMPOUND, new ArrayList<>() );
            for ( Enchantment ench : enchantments )
            {
                CompoundTag enchObj = new CompoundTag();
                enchObj.add( "id", new StringTag( ench.id ) );
                enchObj.add( "lvl", new IntTag( ench.level ) );
                enchArray.add( enchObj );
            }
            root.add( "Enchantments", enchArray );
        }

        if ( unbreakable != null )
        {
            root.add( "Unbreakable", new ByteTag( ( unbreakable ) ? 1 : 0 ) );
        }

        CompoundTag display = new CompoundTag();

        if ( name != null )
        {
            display.add( "Name", new StringTag( ComponentSerializer.toString( name ) ) );
        }

        if ( !lore.isEmpty() )
        {
            ListTag loreTag = new ListTag( Tag.TAG_STRING, new ArrayList<>() );
            for ( BaseComponent[] itLore : lore )
            {
                loreTag.add( new StringTag( ComponentSerializer.toString( itLore ) ) );
            }
            display.add( "Lore", loreTag );
        }

        if ( display.size() != 0 )
        {
            root.add( "display", display );
        }
    }

    public ItemTag toItemTag()
    {
        ItemTag itemTag = new ItemTag();
        itemTag.properties = clone();
        // Lombok converts it to AbstractList when using build(), we want a modifiable ArrayList
        itemTag.properties.setEnchantments( new ArrayList<>( itemTag.properties.getEnchantments() ) );
        itemTag.properties.setLore( new ArrayList<>( itemTag.properties.getLore() ) );
        // ^
        itemTag.getNbt(); // update local field
        return itemTag;
    }

    @Override
    public ItemPropertyInfo clone()
    {
        try
        {
            return (ItemPropertyInfo) super.clone();
        } catch ( CloneNotSupportedException ex )
        {
            return null;
        }
    }
}
