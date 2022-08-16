package de.lundy.lobster.commands.music;

import de.lundy.lobster.lavaplayer.PlayerManager;
import de.lundy.radiogarden.Page;
import de.lundy.radiogarden.RadioGarden;
import de.lundy.radiogarden.places.Place;
import net.dv8tion.jda.api.entities.AudioChannel;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.interaction.command.CommandAutoCompleteInteractionEvent;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.SelectMenuInteractionEvent;
import net.dv8tion.jda.api.exceptions.InsufficientPermissionException;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.components.selections.SelectMenu;
import net.dv8tion.jda.api.interactions.components.selections.SelectOption;
import net.dv8tion.jda.api.managers.AudioManager;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

public class RadioCommand extends ListenerAdapter {

    @Override
    public void onSlashCommandInteraction(@NotNull SlashCommandInteractionEvent event) {

        if (event.getGuild() == null) return;

        if (event.getName().equalsIgnoreCase("radio")) {

            OptionMapping locationOption = event.getOption("location");
            Member member = event.getMember();

            if (!member.getVoiceState().inAudioChannel()) {
                event.reply(":warning: You are not in a voice channel.").setEphemeral(true).queue();
                return;
            }

            if (locationOption == null) {
                event.reply("Please enter a location.").setEphemeral(true).queue();
                return;
            }

            String locationString = locationOption.getAsString();
            List<Place> allPlaces;

            try {
                allPlaces = RadioGarden.getAllPlaces();
            } catch (IOException e) {
                e.printStackTrace();
                return;
            }

            Place selectedPlace = allPlaces.stream().filter(p -> p.getTitle().equalsIgnoreCase(locationString)).toList().get(0);
            Page placePage;
            Page.PageItemInfo[] pageItemInfos;

            try {
                placePage = RadioGarden.getPageFromId(selectedPlace.getId());
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

            pageItemInfos = placePage.getData().getContent().get(0).getItems();
            System.out.println(Arrays.toString(pageItemInfos));
            List<SelectOption> stations = new ArrayList<>();

            Arrays.stream(pageItemInfos).toList().stream().filter(s -> s.getHref() != null).forEach(s -> stations.add(SelectOption.of(s.getTitle(), s.getHref())));

            SelectMenu selectStation = SelectMenu.create("select-station").addOptions(stations).setPlaceholder(placePage.getData().getContent().get(0).getTitle()).build();

            event.reply("Please choose a Radio Station from **" + locationString + "**").addActionRow(selectStation).setEphemeral(true).queue();

        }

    }

    @Override
    public void onCommandAutoCompleteInteraction(@NotNull CommandAutoCompleteInteractionEvent event) {

        System.out.println(event.getFocusedOption());

        if (event.getName().equals("radio") && event.getFocusedOption().getName().equals("location")) {

            List<Place> places;

            try {
                places = RadioGarden.getAllPlaces();
            } catch (IOException e) {
                e.printStackTrace();
                return;
            }

            Collection<String> allSuggestions = new ArrayList<>();
            places.forEach(p -> allSuggestions.add(p.getTitle()));
            Collection<String> filteredSuggestions = allSuggestions.stream().filter(a -> a.contains(event.getFocusedOption().getValue())).toList();

            if (filteredSuggestions.size() > 25) event.replyChoice("Please be more specific", "-").queue();

            event.replyChoiceStrings(filteredSuggestions).queue();

        }

    }

    @Override
    public void onSelectMenuInteraction(@NotNull SelectMenuInteractionEvent event) {

        if (event.getComponentId().equals("select-station")) {

            if (!event.getGuild().getSelfMember().getVoiceState().inAudioChannel()) {

                AudioManager audioManager = event.getGuild().getAudioManager();
                AudioChannel audioChannel = event.getMember().getVoiceState().getChannel();

                try {
                    audioManager.openAudioConnection(audioChannel);
                } catch (InsufficientPermissionException e) {
                    event.reply(":warning: I do not have enough permissions to join that channel.").setEphemeral(true).queue();
                    return;
                }
            }

            String listenId = event.getValues().get(0).split("/")[3];
            event.deferReply().queue();
            PlayerManager.getInstance().loadAndPlay(event.getHook(), RadioGarden.getListenUrl(listenId), false);

        }

    }
}
