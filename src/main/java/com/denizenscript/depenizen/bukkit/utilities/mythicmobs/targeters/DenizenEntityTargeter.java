package com.denizenscript.depenizen.bukkit.utilities.mythicmobs.targeters;

import com.denizenscript.denizen.objects.EntityTag;
import com.denizenscript.denizencore.events.OldEventManager;
import com.denizenscript.denizencore.objects.ObjectTag;
import com.denizenscript.denizencore.objects.core.ListTag;
import com.denizenscript.denizencore.tags.TagContext;
import com.denizenscript.denizencore.tags.TagManager;
import com.denizenscript.denizencore.utilities.CoreUtilities;
import io.lumine.xikage.mythicmobs.adapters.AbstractEntity;
import io.lumine.xikage.mythicmobs.adapters.bukkit.BukkitAdapter;
import io.lumine.xikage.mythicmobs.io.MythicLineConfig;
import io.lumine.xikage.mythicmobs.skills.SkillMetadata;
import io.lumine.xikage.mythicmobs.skills.targeters.IEntitySelector;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

public class DenizenEntityTargeter extends IEntitySelector {
    final String tag;
    OldEventManager.OldEventContextSource source;
    HashMap<String, ObjectTag> context;

    public DenizenEntityTargeter(MythicLineConfig mlc) {
        super(mlc);
        tag = mlc.getString("tag");
        context = new HashMap<>();
        source = new OldEventManager.OldEventContextSource();
        source.contexts = new HashMap<>();
    }

    @Override
    public HashSet<AbstractEntity> getEntities(SkillMetadata skillMetadata) {
        TagContext tagContext = CoreUtilities.noDebugContext.clone();
        tagContext.contextSource = source;
        source.contexts.put("entity", new EntityTag(skillMetadata.getCaster().getEntity().getBukkitEntity()));
        ObjectTag object = TagManager.tagObject(tag, tagContext);
        List<EntityTag> list = object.asType(ListTag.class, tagContext).filter(EntityTag.class, tagContext);
        HashSet<AbstractEntity> entities = new HashSet<AbstractEntity>();
        for (EntityTag entity : list) {
            entities.add(BukkitAdapter.adapt(entity.getBukkitEntity()));
        }
        return entities;
    }
}
