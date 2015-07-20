package my.chatapplication.Navigation;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;

import it.neokree.materialnavigationdrawer.MaterialNavigationDrawer;
import my.chatapplication.Chat.ChatActivity;
import my.chatapplication.Controller.SignInController;
import my.chatapplication.R;
import my.chatapplication.Test;

/**
 * Created by nasser on 16/07/15.
 */
public class NavigationBar extends MaterialNavigationDrawer {
        @Override
        public void init(Bundle savedInstanceState) {

            // create and set the header
            View view = LayoutInflater.from(this).inflate(R.layout.custom_drawer,null);
            setDrawerHeaderCustom(view);

            this.addSection(newSection("Home", new FragmentIndex()));
            this.addSection(newSection("Chat", new Intent(this, ChatActivity.class)));
            this.addSection(newSection("Test", new Intent(this, Test.class)));
            this.addSection(newSection("SignIn", new Intent(this, SignInController.class)));

            //this.addBottomSection(newSection("Bottom Section",R.drawable.ic_settings_black_24dp,new Intent(this,Settings.class)));

        }

}
