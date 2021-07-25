package de.pierreschwang.headdatabase.dao;

import de.pierreschwang.headdatabase.util.SkullHelper;
import org.bukkit.inventory.ItemStack;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

public enum Category {

    HUMANOID("humanoid", "aaa2bd3c906451465998c73a4bd75252ea8d5b0a34c8ca4d670a81bd184a9ed"),
    BLOCKS("blocks", "8449b9318e33158e64a46ab0de121c3d40000e3332c1574932b3c849d8fa0dc2"),
    PLANTS("plants", "ed80c26f904b57e631e39ebc446ec1af2dce3432eb8431fbd19087adb4abcb"),
    FOOD_DRINKS("food-drinks", "9496589fb5c1f69387b7fb17d92312058ff6e8ebeb3eb89e4f73e78196113b"),
    MONSTERS("monsters", "b79e5014609070ff1295bc3a6ddc44976e20201bb0d5e92e52c51555b8a74861"),
    ALPHABET("alphabet", "a67d813ae7ffe5be951a4f41f2aa619a5e3894e85ea5d4986f84949c63d7672e"),
    ANIMALS("animals", "c135f5838e51585e15942e8f2f1cc36da963070ddd5ba35af95ed37eb6c3"),
    DECORATION("decoration", "5ebfd2396cbabdb42c348bcf41599c87a506a71ef60948c496f95c6cb63141"),
    HUMANS("humans", "7bb6672c51777da6d6defc7ea9d4cfac50acc88e9671b29c82f64cc8bfb6f"),
    MISCELLANEOUS("miscellaneous", "bc2b9b9ae622bd68adff7180f8206ec4494abbfa130e94a584ec692e8984ab2");

    private final String key;
    private final String texture;

    Category(String key, String texture) {
        this.key = key;
        this.texture = Base64.getEncoder().encodeToString(
                String.format("{\"textures\":{\"SKIN\":{\"url\":\"https://textures.minecraft.net/texture/%s\"}}}", texture)
                        .getBytes(StandardCharsets.UTF_8)
        );
    }

    public ItemStack toItemStack(String displayName) {
        return SkullHelper.createSkull(texture);
    }

    public static Category byKey(String key) {
        for (Category value : values()) {
            if (value.key.equals(key)) {
                return value;
            }
        }
        return Category.MISCELLANEOUS;
    }

}
