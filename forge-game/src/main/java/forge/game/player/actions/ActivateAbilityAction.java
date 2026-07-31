package forge.game.player.actions;

import forge.game.GameEntityView;
import forge.game.card.Card;
import forge.game.spellability.SpellAbility;

import java.util.Map;
import java.util.TreeMap;

public class ActivateAbilityAction extends PlayerAction {
    private final String abilityDescription;
    private final String abilityCanonicalKey;

    public ActivateAbilityAction(GameEntityView cardView, String abilityDescription) {
        this(cardView, abilityDescription, "");
    }

    public ActivateAbilityAction(GameEntityView cardView, String abilityDescription, String abilityCanonicalKey) {
        super(cardView, "Activate ability");
        this.abilityDescription = abilityDescription == null ? "" : abilityDescription;
        this.abilityCanonicalKey = abilityCanonicalKey == null ? "" : abilityCanonicalKey;
    }

    public String getAbilityDescription() {
        return abilityDescription;
    }

    public String getAbilityCanonicalKey() {
        return abilityCanonicalKey;
    }

    public static String canonicalKey(final SpellAbility ability) {
        if (ability == null) {
            return "<null>";
        }
        final Card host = ability.getHostCard();
        final StringBuilder sb = new StringBuilder();
        sb.append("hostId=").append(host == null ? "<none>" : host.getId());
        sb.append("|hostName=").append(host == null ? "<none>" : host.getName());
        sb.append("|api=").append(describeApi(ability));
        sb.append("|cost=").append(describeCost(ability));
        sb.append("|defined=").append(ability.getParamOrDefault("Defined", ""));
        sb.append("|validTgts=").append(ability.getParamOrDefault("ValidTgts", ""));
        sb.append("|params=").append(describeParams(ability));
        sb.append("|originalParams=").append(describeOriginalParams(ability));
        return sb.toString();
    }

    public static String describeApi(final SpellAbility ability) {
        return ability == null || ability.getApi() == null ? "<none>" : ability.getApi().name();
    }

    public static String describeCost(final SpellAbility ability) {
        if (ability == null || ability.getPayCosts() == null) {
            return "<none>";
        }
        return ability.getPayCosts().toSimpleString();
    }

    public static String describeParams(final SpellAbility ability) {
        return ability == null ? "{}" : describeMap(ability.getMapParams());
    }

    public static String describeOriginalParams(final SpellAbility ability) {
        return ability == null ? "{}" : describeMap(ability.getOriginalMapParams());
    }

    private static String describeMap(final Map<String, String> params) {
        if (params == null || params.isEmpty()) {
            return "{}";
        }
        return new TreeMap<>(params).toString();
    }

    @Override
    protected void appendDetails(final StringBuilder sb) {
        sb.append(" ability=\"").append(abilityDescription).append("\"");
        sb.append(" canonicalKey=\"").append(abilityCanonicalKey).append("\"");
    }
}
