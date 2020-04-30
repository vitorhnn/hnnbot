package br.net.hnn.discordbot.spotify

case class Album(/**
                  * The artists of the album.
                  **/
                 artists: List[Artist],

                 /**
                  * The name of the album. In case of an album takedown, the value may be
                  * an empty string.
                  **/
                 name: String,

                 /**
                  * The tracks of the album.
                  **/
                 tracks: Pager[Track]) {
}