package com.manmademagic.plugins;

import lombok.extern.slf4j.Slf4j;
import com.google.common.eventbus.Subscribe;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.inject.Provides;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.InetSocketAddress;
import java.util.concurrent.Executors;
import javax.inject.Inject;

import net.runelite.api.*;
import net.runelite.api.events.GameStateChanged;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.http.api.RuneLiteAPI;

@Slf4j
@PluginDescriptor(
        name = "Extended HTTP API Server"
)

public class ExtendedHTTPAPIPlugin extends Plugin
{
    @Inject
    private Client client;
    private ItemContainer itemContainer;
    private HttpServer server;
    private StringBuilder sb;

    @Inject
    private ExtendedHTTPAPIConfig config;

    @Provides
    ExtendedHTTPAPIConfig provideConfig(ConfigManager configManager)
    {
        return configManager.getConfig(ExtendedHTTPAPIConfig.class);
    }

    @Override
    protected void startUp() throws Exception
    {
        sb = new StringBuilder("");
        server = HttpServer.create(new InetSocketAddress(config.exposedPort()), 0);
        if (config.exposeWorld() == true)
        {
            server.createContext("/world", new WorldHandler());
            sb.append("<a href='/world'>/world</a><br />") ;
        }
        if (config.exposeUsername() == true)
        {
            server.createContext("/username", new UsernameHandler());
            sb.append("<a href='/username'>/username</a><br />") ;
        }
        if (config.exposeStats() == true)
        {
            server.createContext("/stats", new StatsHandler());
            sb.append("<a href='/stats'>/stats</a><br />") ;
        }
        if (config.exposeInventory() == true)
        {
            server.createContext("/inventory", new InventoryHandler());
            sb.append("<a href='/inventory'>/inventory</a><br />") ;
        }
        if (config.exposeWeight() == true)
        {
            server.createContext("/weight", new WeightHandler());
            sb.append("<a href='/weight'>/weight</a><br />") ;
        }
        if (config.exposeEnergy() == true)
        {
            server.createContext("/energy", new EnergyHandler());
            sb.append("<a href='/energy'>/energy</a><br />") ;
        }
        if (config.exposeQuests() == true) {
            server.createContext("/quests", new QuestsHandler());
            sb.append("<a href='/quests'>/quests</a><br />");
        }
        server.setExecutor(Executors.newSingleThreadExecutor());
        server.createContext("/", new BaseHandler());
        server.start();
    }

    @Override
    protected void shutDown() throws Exception
    {
        server.stop(1);
    }

    @Subscribe
    public void onGameStateChanged(GameStateChanged gameStateChanged)
    {
        if (gameStateChanged.getGameState() == GameState.LOGGED_IN)
        {
            client.addChatMessage(ChatMessageType.GAMEMESSAGE, "","Server started on port " + config.exposedPort(),null);
        }
    }

    class BaseHandler implements HttpHandler
    {
        @Override
        public void handle(HttpExchange exchange) throws IOException
        {
            exchange.sendResponseHeaders(200, sb.toString().getBytes().length);
            OutputStream os = exchange.getResponseBody();
            os.write(sb.toString().getBytes());
            os.close();
        }
    }

    class StatsHandler implements HttpHandler
    {
        @Override
        public void handle(HttpExchange exchange) throws IOException
        {
            exchange.getResponseHeaders().set("Content-Type", "application/json");
            JsonArray skills = new JsonArray();
            for (Skill skill : Skill.values())
            {
                if (skill == Skill.OVERALL)
                {
                    continue;
                }

                JsonObject object = new JsonObject();
                object.addProperty("stat", skill.getName());
                object.addProperty("level", client.getRealSkillLevel(skill));
                object.addProperty("boostedLevel", client.getBoostedSkillLevel(skill));
                object.addProperty("xp", client.getSkillExperience(skill));
                skills.add(object);
            }

            exchange.sendResponseHeaders(200, 0);
            try (OutputStreamWriter out = new OutputStreamWriter(exchange.getResponseBody()))
            {
                RuneLiteAPI.GSON.toJson(skills, out);
            }
        }
    }

    class QuestsHandler implements HttpHandler
    {
        @Override
        public void handle(HttpExchange exchange) throws IOException
        {
            exchange.getResponseHeaders().set("Content-Type", "application/json");
            JsonArray quests = new JsonArray();
            for (Quest quest : Quest.values())
            {
                JsonObject object = new JsonObject();
                object.addProperty("quest", quest.getName());
               switch (quest.getState(client))
               {
                   case NOT_STARTED:
                       object.addProperty("status", "Not Started");
                       break;
                   case IN_PROGRESS:
                       object.addProperty("status", "In Progress");
                       break;
                   case FINISHED:
                       object.addProperty("status", "Finished");
                       break;
                   default:
                       object.addProperty("status", "Unknown");
                       break;
               }
                quests.add(object);
            }

            exchange.sendResponseHeaders(200, 0);
            try (OutputStreamWriter out = new OutputStreamWriter(exchange.getResponseBody()))
            {
                RuneLiteAPI.GSON.toJson(quests, out);
            }
        }
    }

    class UsernameHandler implements HttpHandler
    {
        @Override
        public void handle(HttpExchange exchange) throws IOException
        {
            exchange.getResponseHeaders().set("Content-Type", "application/json");
            exchange.sendResponseHeaders(200, 0);
            try (OutputStreamWriter out = new OutputStreamWriter(exchange.getResponseBody()))
            {
                RuneLiteAPI.GSON.toJson(client.getUsername(), out);
            }
        }
    }
    class WorldHandler implements HttpHandler
    {
        @Override
        public void handle(HttpExchange exchange) throws IOException
        {
            exchange.getResponseHeaders().set("Content-Type", "application/json");
            exchange.sendResponseHeaders(200, 0);
            try (OutputStreamWriter out = new OutputStreamWriter(exchange.getResponseBody()))
            {
                RuneLiteAPI.GSON.toJson(client.getWorld(), out);
            }
        }
    }
    class WeightHandler implements HttpHandler
    {
        @Override
        public void handle(HttpExchange exchange) throws IOException
        {
            exchange.getResponseHeaders().set("Content-Type", "application/json");
            exchange.sendResponseHeaders(200, 0);
            try (OutputStreamWriter out = new OutputStreamWriter(exchange.getResponseBody()))
            {
                RuneLiteAPI.GSON.toJson(client.getWeight(), out);
            }
        }
    }
    class EnergyHandler implements HttpHandler
    {
        @Override
        public void handle(HttpExchange exchange) throws IOException
        {
            exchange.getResponseHeaders().set("Content-Type", "application/json");
            exchange.sendResponseHeaders(200, 0);
            try (OutputStreamWriter out = new OutputStreamWriter(exchange.getResponseBody()))
            {
                RuneLiteAPI.GSON.toJson(client.getEnergy(), out);
            }
        }
    }
    class InventoryHandler implements HttpHandler
    {
        @Override
        public void handle(HttpExchange exchange) throws IOException
        {
            exchange.getResponseHeaders().set("Content-Type", "application/json");
            // Get the inventory
           ItemContainer inventory = client.getItemContainer(InventoryID.INVENTORY);
           JsonArray items = new JsonArray();

           int position = 1;
           for (Item item : inventory.getItems())
           {
               ItemComposition currentItem = client.getItemDefinition(item.getId());
               if (item.getId() != -1)
               {
                   JsonObject object = new JsonObject();
                   object.addProperty("id", item.getId());
                   object.addProperty("position", position);
                   object.addProperty("name", currentItem.getName());
                   object.addProperty("quantity", item.getQuantity());
                   object.addProperty("haPrice", currentItem.getHaPrice());
                   object.addProperty("price", currentItem.getPrice());
                   items.add(object);
               }

               position++;
           }

            exchange.sendResponseHeaders(200, 0);
            try (OutputStreamWriter out = new OutputStreamWriter(exchange.getResponseBody()))
            {
                RuneLiteAPI.GSON.toJson(items, out);
            }
        }
    }
}