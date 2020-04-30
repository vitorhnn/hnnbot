package br.net.hnn.discordbot.spotify

case class Track(/**
                  * The artists who performed the track. Each artist object includes a
                  * link in href to more detailed information about the artist.
                  * hnn note: I get why this is here, but why is there an artist array on
                  * Album too?
                  */
                 artists: List[Artist],

                 /**
                  * The name of the track.
                  **/
                 name: String) {
}