require "spec_helper"
feature "sign in" do
  scenario "I should be able to visit the sign in page" do
    visit '/sign_in'
    fill_in('username', with: 'zero')
    fill_in('password', with: '12345')
    click_button("Submit")
    expect(page).to have_content 'Thank you for signing in!'
  end
end
