package de.joshicodes.rja.requests.rest.server.member;

import com.google.gson.JsonElement;
import de.joshicodes.rja.RJA;
import de.joshicodes.rja.object.server.Member;
import de.joshicodes.rja.requests.rest.RestRequest;

public class FetchMemberRequest extends RestRequest<Member> {

    public FetchMemberRequest(String serverId, String memberId) {
        super("GET", "/servers/" + serverId + "/members/" + memberId);
    }

    @Override
    public Member fetch(RJA rja, int statusCode, JsonElement data) {
        if(statusCode != 200) return null;
        Member m = Member.from(rja, data.getAsJsonObject());
        rja.cacheMember(m);
        return m;
    }

}
