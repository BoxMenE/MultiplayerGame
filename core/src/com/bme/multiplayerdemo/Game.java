package com.bme.multiplayerdemo;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.URISyntaxException;

import io.socket.client.IO;
import io.socket.client.Socket;
import io.socket.client.SocketIOException;
import io.socket.emitter.Emitter;
import jdk.nashorn.api.scripting.JSObject;

public class Game extends ApplicationAdapter {
	private Socket socket;

	SpriteBatch batch;
	Texture img;
	
	@Override
	public void create () {
		batch = new SpriteBatch();
		img = new Texture("badlogic.jpg");

		connectSocket();
		configSocketEvents();
	}

	@Override
	public void render () {
		Gdx.gl.glClearColor(1, 0, 0, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		batch.begin();
		batch.draw(img, 0, 0);
		batch.end();
	}
	
	@Override
	public void dispose () {
		batch.dispose();
		img.dispose();
	}


	public void connectSocket(){
		try{
			socket = IO.socket("http://localhost:8080");
			socket.connect();
			System.out.println("Connected");
		}
		catch(URISyntaxException u){
			System.out.println(u);
		}
	}

	public void configSocketEvents(){
		socket.on(Socket.EVENT_CONNECT, new Emitter.Listener() {
			@Override
			public void call(Object... objects) {
				Gdx.app.log("SocketIO", "Connected");
			}
		}).on("socketID", new Emitter.Listener() {
			@Override
			public void call(Object... objects) {
				JSONObject data = (JSONObject) objects[0];
				try{
					String id = data.getString("id");
					Gdx.app.log("SocketIO", "My ID: " + id);
				}
				catch(JSONException j){
					System.out.println(j);
				}

			}
		}).on("newPlayer", new Emitter.Listener() {
			@Override
			public void call(Object... objects) {
				JSONObject data = (JSONObject) objects[0];
				try{
					String id = data.getString("id");
					Gdx.app.log("SocketIO", "New Player ID: " + id);
				}
				catch(JSONException j){
					System.out.println(j);
				}
			}
		}).on("disconnectedPlayer", new Emitter.Listener() {
			@Override
			public void call(Object... objects) {
				JSONObject obj = (JSONObject) objects[0];
				try{
					String id = obj.getString("id");
					Gdx.app.log("SocketIO","Player Disconnected: " + id);
				}
				catch(JSONException e){
					System.out.println(e);
				}

			}
		});
	}
}
