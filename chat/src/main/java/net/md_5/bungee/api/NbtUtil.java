package net.md_5.bungee.api;

import java.util.Iterator;
import java.util.regex.Pattern;
import se.llbit.nbt.ByteArrayTag;
import se.llbit.nbt.ByteTag;
import se.llbit.nbt.CompoundTag;
import se.llbit.nbt.DoubleTag;
import se.llbit.nbt.FloatTag;
import se.llbit.nbt.IntArrayTag;
import se.llbit.nbt.IntTag;
import se.llbit.nbt.ListTag;
import se.llbit.nbt.LongArrayTag;
import se.llbit.nbt.LongTag;
import se.llbit.nbt.NamedTag;
import se.llbit.nbt.ShortTag;
import se.llbit.nbt.StringTag;
import se.llbit.nbt.Tag;

public class NbtUtil
{

    /**
     * Pattern to test whether String needs escaping.
     */
    private static final Pattern PATTERN = Pattern.compile( "[A-Za-z0-9._+-]+" );

    // Various JSON syntax properties
    private static final char COMPOUND_BEGIN = '{';
    private static final char COMPOUND_END = '}';
    private static final char ARRAY_BEGIN = '[';
    private static final char ARRAY_END = ']';
    private static final char ARRAY_SEPARATOR = ',';
    private static final char KEY_VALUE_SEPARATOR = ':';
    /**/

    /**
     * Converts a raw NBT string serialised by a Mojangson format to a CompoundTag.
     *
     * @param string the string to get from
     * @return the tag
     */
    public static CompoundTag fromString(String string)
    {
        CompoundTag tag = new CompoundTag();
        if ( string.equals( COMPOUND_BEGIN + "" + COMPOUND_END ) )
        {
            return tag;
        }

        // TODO

        return tag;
    }

    /**
     * Converts NBT tag to a format used by Mojangson.
     *
     * @param tag the tag to convert
     * @return the converted string
     */
    public static String toString(Tag tag)
    {
        String val = null;
        if ( tag instanceof NamedTag )
        {
            val = toString( ( (NamedTag) tag ).getTag() );
        } else if ( tag instanceof CompoundTag )
        {
            StringBuilder builder = new StringBuilder();
            builder.append( COMPOUND_BEGIN );

            for ( NamedTag next : (CompoundTag) tag )
            {
                if ( builder.length() != 1 )
                {
                    builder.append( ARRAY_SEPARATOR );
                }

                if ( PATTERN.matcher( next.name ).matches() )
                {
                    builder.append( next.name );
                } else
                {
                    // escape the name
                    StringBuilder builder1 = new StringBuilder();
                    builder1.append( "\"" );

                    for ( int i = 0; i < next.name.length(); ++i )
                    {
                        char ch = next.name.charAt( i );
                        if ( ch == '\\' || ch == '"' )
                        {
                            builder1.append( '\\' );
                        }

                        builder1.append( ch );
                    }

                    builder1.append( '"' );
                    builder.append( builder1 );
                }

                builder.append( KEY_VALUE_SEPARATOR ).append( toString( next ) );
            }

            val = builder.append( COMPOUND_END ).toString();
        } else if ( tag instanceof IntTag )
        {
            val = Integer.toString( ( (IntTag) tag ).value );
        } else if ( tag instanceof ByteTag )
        {
            val = ( (ByteTag) tag ).value + "b";
        } else if ( tag instanceof DoubleTag )
        {
            val = ( (DoubleTag) tag ).value + "d";
        } else if ( tag instanceof FloatTag )
        {
            val = ( (FloatTag) tag ).value + "f";
        } else if ( tag instanceof LongTag )
        {
            val = ( (LongTag) tag ).value + "L";
        } else if ( tag instanceof ShortTag )
        {
            val = ( (ShortTag) tag ).value + "s";
        } else if ( tag instanceof StringTag )
        {
            StringTag stringTag = (StringTag) tag;

            StringBuilder builder = new StringBuilder( "\"" );

            for ( int i = 0; i < stringTag.value.length(); ++i )
            {
                char ch = stringTag.value.charAt( i );
                if ( ch == '\\' || ch == '"' )
                {
                    builder.append( '\\' );
                }
                builder.append( ch );
            }
            val = builder.append( '"' ).toString();
        } else if ( tag instanceof ListTag )
        {
            ListTag list = (ListTag) tag;

            StringBuilder builder = new StringBuilder();
            builder.append( ARRAY_BEGIN );

            for ( int i = 0; i < list.items.size(); ++i )
            {
                if ( i != 0 )
                {
                    builder.append( ARRAY_SEPARATOR );
                }

                builder.append( toString( list.items.get( i ) ) );
            }

            val = builder.append( ARRAY_END ).toString();
        } else if ( tag instanceof ByteArrayTag )
        {
            ByteArrayTag arrayTag = (ByteArrayTag) tag;

            StringBuilder builder = new StringBuilder();
            builder.append( ARRAY_BEGIN );
            builder.append( "B;" );
            for ( int i = 0; i < arrayTag.value.length; ++i )
            {
                if ( i != 0 )
                {
                    builder.append( ARRAY_SEPARATOR );
                }
                builder.append( arrayTag.value[ i ] ).append( 'B' );
            }

            val = builder.append( ARRAY_END ).toString();
        } else if ( tag instanceof IntArrayTag )
        {
            IntArrayTag arrayTag = (IntArrayTag) tag;

            StringBuilder builder = new StringBuilder();
            builder.append( ARRAY_BEGIN );
            builder.append( "I;" );
            for ( int i = 0; i < arrayTag.value.length; ++i )
            {
                if ( i != 0 )
                {
                    builder.append( ARRAY_SEPARATOR );
                }
                builder.append( arrayTag.value[ i ] );
            }

            val = builder.append( ARRAY_END ).toString();
        } else if ( tag instanceof LongArrayTag )
        {
            LongArrayTag arrayTag = (LongArrayTag) tag;

            StringBuilder builder = new StringBuilder();
            builder.append( ARRAY_BEGIN );
            builder.append( "L;" );
            for ( int i = 0; i < arrayTag.value.length; ++i )
            {
                if ( i != 0 )
                {
                    builder.append( ARRAY_SEPARATOR );
                }
                builder.append( arrayTag.value[ i ] ).append( 'L' );
            }

            val = builder.append( ARRAY_END ).toString();
        }

        if ( val == null )
        {
            throw new UnsupportedOperationException( "Unimplemented tag type " + tag.tagName() + " (" + tag.tagName() + "|" + tag.getClass().getSimpleName() + ")" );
        }

        return val;
    }

    public static boolean has(CompoundTag tag, String property)
    {
        Iterator<NamedTag> iterator = tag.iterator();
        boolean flag = false;
        while ( iterator.hasNext() )
        {
            NamedTag tag1 = iterator.next();
            if ( tag1.isNamed( property ) )
            {
                flag = true;
                break;
            }
        }
        return flag;
    }
}
