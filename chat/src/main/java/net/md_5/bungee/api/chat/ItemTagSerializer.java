package net.md_5.bungee.api.chat;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import java.lang.reflect.Type;

public class ItemTagSerializer implements JsonSerializer<ItemTag>, JsonDeserializer<ItemTag>
{

    @Override
    public ItemTag deserialize(JsonElement element, Type type, JsonDeserializationContext context) throws JsonParseException
    {
        // Remove the enclosing string quotes.
        String eString = element.toString();
        if ( eString.length() > 2 && eString.charAt( 0 ) == '\"' && eString.charAt( eString.length() - 1 ) == '\"' )
        {
            eString = eString.substring( 1, eString.length() - 1 );
        }

        return ItemTag.ofNbt( eString );
    }

    @Override
    public JsonElement serialize(ItemTag itemTag, Type type, JsonSerializationContext context)
    {
        return context.serialize( itemTag.getNbt() );
    }
}
